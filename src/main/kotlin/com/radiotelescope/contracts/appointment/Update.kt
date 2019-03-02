package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import java.util.*

/**
 * Command class for editing an appointment
 *
 * @param request the [Update.Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param telescopeRepo the [ITelescopeRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
class Update(
        private val request: Update.Request,
        private val appointmentRepo: IAppointmentRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
):  Command<Long, Multimap<ErrorTag,String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will update and persist the [Appointment] object and
     * return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val appointment = appointmentRepo.findById(request.id).get()
        val updatedAppointment = appointmentRepo.save(request.updateEntity(appointment))
        return SimpleResult(updatedAppointment.id, null)
    }

    /**
     * Method responsible for constraint checking and validations for the appointment
     * update request. It will ensure the appointment and telescope exist.
     * It will ensure that the startTime is after the current time and
     * endTime is after the startTime.
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        var errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (appointmentRepo.existsById(id)) {
                if(telescopeRepo.existsById(telescopeId)) {
                    if (startTime.before(Date()))
                        errors.put(ErrorTag.START_TIME, "New start time cannot be before the current time")
                    if (endTime.before(startTime) || endTime == startTime)
                        errors.put(ErrorTag.END_TIME, "New end time cannot be less than or equal to the new start time")
                    if (hours < 0 || hours >= 24)
                        errors.put(ErrorTag.HOURS, "Hours must be between 0 and 24")
                    if (minutes < 0 || minutes >= 60)
                        errors.put(ErrorTag.MINUTES, "Minutes must be between 0 and 60")
                    if (seconds < 0 || seconds >= 60)
                        errors.put(ErrorTag.SECONDS, "Seconds must be between 0 and 60")
                    if (declination > 90 || declination < -90)
                        errors.put(ErrorTag.DECLINATION, "Declination must be between -90 - 90")
                    if (isOverlap())
                        errors.put(ErrorTag.OVERLAP, "Appointment time is conflicted with another appointment")
                }
                else{
                    errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId not found")
                    return errors
                }
                val userId = appointmentRepo.findById(id).get().user.id
                if(userRoleRepo.findMembershipRoleByUserId(userId) == null)
                    errors.put(ErrorTag.CATEGORY_OF_SERVICE, "User's Category of Service has not yet been approved")
            } else {
                errors.put(ErrorTag.ID, "Appointment #$id not found")
                return errors
            }
        }

        if (!errors.isEmpty)
            return errors

        errors = validateAvailableAllottedTime()

        return errors
    }

    /**
     * Method responsible for checking if a user has enough available time
     * to schedule the new observation, as well as having a membership role
     */
    private fun validateAvailableAllottedTime(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            val theAppointment = appointmentRepo.findById(id).get()
            val newTime = endTime.time - startTime.time
            val allottedTime = allottedTimeCapRepo.findByUserId(theAppointment.user.id).allottedTime

            // Free up the time associated with this appointment since it
            // may have changed
            val totalTime = determineCurrentUsedTime(theAppointment)

            // If allottedTime = null, they have no limit, otherwise check it
            if(allottedTime != null && (totalTime + newTime) > allottedTime){
                val hours = allottedTime / (60*60*1000)
                errors.put(ErrorTag.ALLOTTED_TIME, "You may only have $hours hours of observation time.")
            }
        }

        return errors
    }

    /**
     * Determine the amount of allotted time currently being used by the user.
     * This more or less just frees up the allotted time for the database record
     * so it can check against the new (or same) time that was passed in with the
     * request
     *
     * @param theAppointment the [Appointment]
     */
    private fun determineCurrentUsedTime(theAppointment: Appointment): Long {
        var totalTime = appointmentRepo.findTotalScheduledAppointmentTimeForUser(theAppointment.user.id) ?: 0

        // This could potentially be 0 if the appointment being
        // updated is a requested appointment and no others exist
        if (totalTime != 0L)
            totalTime -= (theAppointment.endTime.time - theAppointment.startTime.time)

        return totalTime
    }

    /**
     * Method responsible for check if the requested appointment
     * conflict with the one that are already scheduled
     */
    private fun isOverlap(): Boolean {
        var isOverlap = false
        val appointmentList = appointmentRepo.findConflict(
                endTime = request.endTime,
                startTime = request.startTime,
                telescopeId = request.telescopeId
        )

        if (appointmentList.size > 1)
            isOverlap = true
        else if (appointmentList.size == 1 && appointmentList[0].id != request.id)
            isOverlap = true

        return isOverlap
    }

    /**
     * Data class containing all fields necessary for appointment update. Implements the
     * [BaseUpdateRequest] interface and overrides the [BaseUpdateRequest.updateEntity]
     * method
     */
    data class Request(
            var id: Long,
            val telescopeId: Long,
            val startTime: Date,
            val endTime: Date,
            val isPublic: Boolean,
            val hours: Int,
            val minutes: Int,
            val seconds: Int,
            val declination: Double
    ): BaseUpdateRequest<Appointment> {
        /**
         * Override of the [BaseUpdateRequest.updateEntity] method that
         * takes an [Appointment] and will update all of its values to the
         * values in the request
         */
        override fun updateEntity(entity: Appointment): Appointment {
            entity.telescopeId = telescopeId
            entity.startTime = startTime
            entity.endTime = endTime
            entity.isPublic = isPublic

            if (entity.coordinate != null) {
                entity.coordinate!!.hours = hours
                entity.coordinate!!.minutes = minutes
                entity.coordinate!!.seconds = seconds
                entity.coordinate!!.declination = declination
                entity.coordinate!!.rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = hours,
                        minutes = minutes,
                        seconds = seconds
                )
            }

            return entity
        }
    }
}