package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
import java.util.*

/**
 * Command class for editing an appointment
 *
 * @param request of type [Update.Request]
 * @param appointmentRepo of type [IAppointmentRepository]
 * @param telescopeRepo of type [ITelescopeRepository]
 *
 */
class Update(
        private val request: Update.Request,
        private val appointmentRepo: IAppointmentRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val userRoleRepo: IUserRoleRepository
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
                    if (rightAscension > 360 || rightAscension < 0)
                        errors.put(ErrorTag.RIGHT_ASCENSION, "Right Ascension must be between 0 - 360")
                    if (declination > 90 || declination < 0)
                        errors.put(ErrorTag.DECLINATION, "Declination must be between 0 - 90")
                    if (isOverlap())
                        errors.put(ErrorTag.OVERLAP, "Appointment time is conflicted with another appointment")
                }
                else{
                    errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId not found")
                    return errors
                }
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
            val theUserRole = userRoleRepo.findMembershipRoleByUserId(theAppointment.user!!.id)

            if (theUserRole == null) {
                errors.put(ErrorTag.CATEGORY_OF_SERVICE, "User's Category of Service has not yet been approved")
                return errors
            }

            // Free up the time associated with this appointment since it
            // may have changed
            val totalTime = determineCurrentUsedTime(theAppointment)

            when (theUserRole.role) {
                UserRole.Role.GUEST -> {
                    if ((totalTime + newTime) > Appointment.GUEST_APPOINTMENT_TIME_CAP)
                        errors.put(ErrorTag.ALLOTTED_TIME, "You may only have up to 5 hours of observation time as a Guest")
                }
                else -> {
                    if ((totalTime + newTime) > Appointment.OTHER_USERS_APPOINTMENT_TIME_CAP)
                        errors.put(ErrorTag.ALLOTTED_TIME, "Max allotted observation time is 50 hours at any given time")
                }
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
        var totalTime = appointmentRepo.findTotalScheduledAppointmentTimeForUser(theAppointment.user!!.id) ?: 0

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
        val listAppts = appointmentRepo.findConflict(
                endTime = request.endTime,
                startTime = request.startTime,
                telescopeId = request.telescopeId
        )

        if(listAppts.size > 1)
            isOverlap = true
        else if(listAppts.size == 1 && listAppts[0].id != request.id)
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
            val rightAscension: Double,
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
                entity.coordinate!!.declination = declination
                entity.coordinate!!.rightAscension = rightAscension
            }

            return entity
        }
    }
}