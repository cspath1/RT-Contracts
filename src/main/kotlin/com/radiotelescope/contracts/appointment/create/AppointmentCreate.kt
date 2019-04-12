package com.radiotelescope.contracts.appointment.create

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*


/**
 * Interface containing logic/fields common to all Appointment Create commands
 */
interface AppointmentCreate {
    /**
     * Abstract class containing all fields common to Appointment Create request objects
     *
     * @property userId the User id
     * @property startTime the Appointment's start time
     * @property endTime the Appointment's end time
     * @property telescopeId the Telescope id
     * @property isPublic whether the appointment is public or not
     * @property priority the Appointment's priority
     */
    abstract class Request : BaseCreateRequest<Appointment> {
        abstract val userId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val telescopeId: Long
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
                priority = request.priority
        )

        if (!appointmentList.isEmpty()) {
            isOverlap = true
        }

        return isOverlap
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
    ): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            val newAppointmentTime = endTime.time - startTime.time
            val totalTime = appointmentRepo.findTotalScheduledAppointmentTimeForUser(userId) ?: 0
            val allottedTime = allottedTimeCapRepo.findByUserId(userId).allottedTime

            // If allottedTime = null, they have no limit, otherwise check it
            if(allottedTime != null && (totalTime + newAppointmentTime) > allottedTime) {
                val hours = allottedTime / (60*60*1000)
                errors.put(ErrorTag.ALLOTTED_TIME, "You may only have $hours hours of observation time.")
            }
        }

        return errors
    }

    /**
     * Method responsible for constraint checking and validations for the
     * appointment create request. It will ensure that both the user and telescope
     * id exist and that the appointment's end time is not before its start time.
     * It also ensures that the start time is not before the current date.
     *
     * @param request the [Request] object
     * @param userRepo the [IUserRepository] interface
     * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
     * @param appointmentRepo the [IRadioTelescopeRepository] interface
     * @return a [HashMultimap] of errors or null
     */
    fun basicValidateRequest(
            request: Request,
            userRepo: IUserRepository,
            radioTelescopeRepo: IRadioTelescopeRepository,
            appointmentRepo: IAppointmentRepository,
            allottedTimeCapRepo: IAllottedTimeCapRepository
    ): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        with(request) {
            if (!userRepo.existsById(userId)) {
                errors.put(ErrorTag.USER_ID, "User #$userId could not be found")
                return errors
            }
            if (!radioTelescopeRepo.existsById(telescopeId)) {
                errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId could not be found")
                return errors
            }
            if(!allottedTimeCapRepo.existsByUserId(userId)) {
                errors.put(ErrorTag.ALLOTTED_TIME_CAP, "Allotted Time Cap for userId $userId could not be found")
                return errors
            }
            if (startTime.after(endTime))
                errors.put(ErrorTag.END_TIME, "Start time must be before end time")
            if (startTime.before(Date()))
                errors.put(ErrorTag.START_TIME, "Start time must be after the current time")
            if (isOverlap(request, appointmentRepo))
                errors.put(ErrorTag.OVERLAP, "Appointment time is conflicted with another appointment")
        }

        return if (errors.isEmpty) null else errors
    }
}