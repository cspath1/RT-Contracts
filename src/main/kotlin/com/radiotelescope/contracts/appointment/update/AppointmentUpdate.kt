package com.radiotelescope.contracts.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.isNotEmpty
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import java.util.*

/**
 * Interface containing logic/fields common to all Appointment Update commands
 */
interface AppointmentUpdate {
    /**
     * Abstract class containing all fields common to Appointment Update requests
     *
     * @property id the Appointment id
     * @property startTime the Appointment's start time
     * @property endTime the Appointment's end time
     * @property telescopeId the Telescope id
     * @property isPublic whether the appointment is public or not
     * @property priority the Appointment's priority
     */
    abstract class Request : BaseUpdateRequest<Appointment> {
        abstract var id: Long
        abstract val telescopeId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val isPublic: Boolean
        abstract val priority: Appointment.Priority
    }

    /**
     * Method responsible for check if the requested appointment
     * conflict with the one that are already scheduled
     *
     * @param request the [Request]
     * @param appointmentRepo the [IAppointmentRepository] interface
     * @return true or false
     */
    fun isOverlap(
            request: Request,
            appointmentRepo: IAppointmentRepository
    ): Boolean {
        var isOverlap = false
        val appointmentList = appointmentRepo.findConflict(
                endTime = request.endTime,
                startTime = request.startTime,
                telescopeId = request.telescopeId,
                priority = request.priority.toString()
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
     *
     * @param request the [Request]
     * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
     * @param appointmentRepo the [IAppointmentRepository] interface
     * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
     * @return a [HashMultimap] of errors or null
     */
    fun baseRequestValidation(
            request: Request,
            radioTelescopeRepo: IRadioTelescopeRepository,
            appointmentRepo: IAppointmentRepository,
            allottedTimeCapRepo: IAllottedTimeCapRepository,
            heartbeatMonitorRepo: IHeartbeatMonitorRepository,
            profile: Profile
    ): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (!appointmentRepo.existsById(id))
                errors.put(ErrorTag.ID, "Appointment #$id not found")
            if (!radioTelescopeRepo.existsById(telescopeId))
                errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId not found")

            if (errors.isNotEmpty())
                return errors

            val theAppointment = appointmentRepo.findById(id).get()

            if(!allottedTimeCapRepo.existsByUserId(theAppointment.user.id)) {
                errors.put(ErrorTag.ALLOTTED_TIME_CAP, "Allotted Time Cap for userId ${theAppointment.user.id} could not be found")
                return errors
            }

            if (theAppointment.status != Appointment.Status.REQUESTED && theAppointment.status != Appointment.Status.SCHEDULED)
                errors.put(ErrorTag.STATUS, "Appointment must be requested or scheduled in order to modify it")

            if (startTime.before(Date()))
                errors.put(ErrorTag.START_TIME, "New start time cannot be before the current time")
            if (endTime.before(startTime) || endTime == startTime)
                errors.put(ErrorTag.END_TIME, "New end time cannot be less than or equal to the new start time")
            if (isOverlap(this, appointmentRepo))
                errors.put(ErrorTag.OVERLAP, "Appointment time is conflicted with another appointment")
            if (profile == Profile.PROD || profile == Profile.TEST) {
                if (!determineInternetConnectivity(telescopeId, heartbeatMonitorRepo))
                    errors.put(ErrorTag.CONNECTION, "No internet connectivity between the remote and the control room has been established")
            }
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
     *
     * @param request the [Request]
     * @param appointmentRepo the [IAppointmentRepository] interface
     * @param userRoleRepo the [IUserRoleRepository] interface
     * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
     * @return a [HashMultimap] of errors or null
     */
    fun validateAvailableAllottedTime(
            request: Request,
            appointmentRepo: IAppointmentRepository,
            userRoleRepo: IUserRoleRepository,
            allottedTimeCapRepo: IAllottedTimeCapRepository
    ): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            val theAppointment = appointmentRepo.findById(id).get()
            val newTime = endTime.time - startTime.time
            val allottedTime = allottedTimeCapRepo.findByUserId(theAppointment.user.id).allottedTime

            // Free up the time associated with this appointment since it
            // may have changed
            val totalTime = determineCurrentUsedTime(theAppointment, appointmentRepo)

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
     * @param appointmentRepo the [IAppointmentRepository] interface
     * @return the total used time in milliseconds
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

    private fun determineInternetConnectivity(
            telescopeId: Long,
            heartbeatMonitorRepo: IHeartbeatMonitorRepository
    ): Boolean {
        val monitor = heartbeatMonitorRepo.findByRadioTelescopeId(telescopeId)!!

        val now = Date()
        val fiveMinutesAgo = Date(now.time - (1000 * 60 * 5))

        return monitor.lastCommunication > fiveMinutesAgo
    }
}