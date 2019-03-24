package com.radiotelescope.contracts.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.isNotEmpty
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import java.util.*

interface AppointmentUpdate {
    abstract class Request : BaseUpdateRequest<Appointment> {
        abstract var id: Long
        abstract val telescopeId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val isPublic: Boolean
    }

    /**
     * Method responsible for check if the requested appointment
     * conflict with the one that are already scheduled
     */
    fun isOverlap(
            request: Request,
            appointmentRepo: IAppointmentRepository
    ): Boolean {
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
     * Method responsible for constraint checking and validations for the
     * appointment update request.
     */
    fun baseRequestValidation(
            request: Request,
            telescopeRepo: ITelescopeRepository,
            appointmentRepo: IAppointmentRepository
    ): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (!appointmentRepo.existsById(id))
                errors.put(ErrorTag.ID, "Appointment #$id not found")
            if (!telescopeRepo.existsById(telescopeId))
                errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId not found")

            if (errors.isNotEmpty())
                return errors

            val theAppointment = appointmentRepo.findById(id).get()

            if (theAppointment.status != Appointment.Status.REQUESTED && theAppointment.status != Appointment.Status.SCHEDULED)
                errors.put(ErrorTag.STATUS, "Appointment must be requested or scheduled in order to modify it")

            if (startTime.before(Date()))
                errors.put(ErrorTag.START_TIME, "New start time cannot be before the current time")
            if (endTime.before(startTime) || endTime == startTime)
                errors.put(ErrorTag.END_TIME, "New end time cannot be less than or equal to the new start time")
            if (isOverlap(this, appointmentRepo))
                errors.put(ErrorTag.OVERLAP, "Appointment time is conflicted with another appointment")
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Delete an appointment
     *
     * @param appointment the Appointment
     * @param appointmentRepo the [IAppointmentRepository] interface
     * @param coordinateRepo the [ICoordinateRepository] interface
     * @param orientationRepo the [IOrientationRepository] interface
     */
    fun deleteAppointment(
            appointment: Appointment,
            appointmentRepo: IAppointmentRepository,
            coordinateRepo: ICoordinateRepository,
            orientationRepo: IOrientationRepository
    ) {
        if (appointment.type == Appointment.Type.POINT || appointment.type == Appointment.Type.RASTER_SCAN) {
            val coordinateList = appointment.coordinateList
            appointment.coordinateList = mutableListOf()

            coordinateRepo.deleteAll(coordinateList)
            appointmentRepo.delete(appointment)
        } else if (appointment.type == Appointment.Type.DRIFT_SCAN) {
            orientationRepo.delete(appointment.orientation!!)
            appointmentRepo.delete(appointment)
        } else if (appointment.type == Appointment.Type.CELESTIAL_BODY) {
            appointment.celestialBody = null
            appointmentRepo.delete(appointment)
        }
    }

    /**
     * Method responsible for checking if a user has enough available time
     * to schedule the new observation, as well as having a membership role
     */
    fun validateAvailableAllottedTime(
            request: Request,
            appointmentRepo: IAppointmentRepository,
            userRoleRepo: IUserRoleRepository
    ): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            val theAppointment = appointmentRepo.findById(id).get()
            val newTime = endTime.time - startTime.time
            val theUserRole = userRoleRepo.findMembershipRoleByUserId(theAppointment.user.id)

            if (theUserRole == null) {
                errors.put(com.radiotelescope.contracts.appointment.ErrorTag.CATEGORY_OF_SERVICE, "User's Category of Service has not yet been approved")
                return errors
            }

            // Free up the time associated with this appointment since it
            // may have changed
            val totalTime = determineCurrentUsedTime(theAppointment, appointmentRepo)

            when (theUserRole.role) {
                com.radiotelescope.repository.role.UserRole.Role.GUEST -> {
                    if ((totalTime + newTime) > com.radiotelescope.repository.appointment.Appointment.GUEST_APPOINTMENT_TIME_CAP)
                        errors.put(com.radiotelescope.contracts.appointment.ErrorTag.ALLOTTED_TIME, "You may only have up to 5 hours of observation time as a Guest")
                }
                else -> {
                    if ((totalTime + newTime) > com.radiotelescope.repository.appointment.Appointment.OTHER_USERS_APPOINTMENT_TIME_CAP)
                        errors.put(com.radiotelescope.contracts.appointment.ErrorTag.ALLOTTED_TIME, "Max allotted observation time is 50 hours at any given time")
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
    fun determineCurrentUsedTime(
            theAppointment: Appointment,
            appointmentRepo: IAppointmentRepository
    ): Long {
        var totalTime = appointmentRepo.findTotalScheduledAppointmentTimeForUser(theAppointment.user.id) ?: 0

        // This could potentially be 0 if the appointment being
        // updated is a requested appointment and no others exist
        if (totalTime != 0L)
            totalTime -= (theAppointment.endTime.time - theAppointment.startTime.time)

        return totalTime
    }
}