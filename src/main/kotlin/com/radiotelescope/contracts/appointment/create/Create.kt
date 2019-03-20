package com.radiotelescope.contracts.appointment.create

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import java.util.*

interface Create {
    abstract class Request : BaseCreateRequest<Appointment> {
        abstract val userId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val telescopeId: Long
        abstract val isPublic: Boolean
    }

    /**
     * Method responsible for check if the requested appointment
     * conflict with the one that are already scheduled
     */
    fun isOverlap(request: Request, appointmentRepo: IAppointmentRepository): Boolean {
        var isOverlap = false
        val appointmentList = appointmentRepo.findConflict(
                endTime = request.endTime,
                startTime = request.startTime,
                telescopeId = request.telescopeId
        )

        if (!appointmentList.isEmpty()) {
            isOverlap = true
        }

        return isOverlap
    }

    /**
     * Method responsible for checking if a user has enough available time
     * to schedule the new observation, as well as having a membership role
     */
    fun validateAvailableAllottedTime(
            request: Request,
            appointmentRepo: IAppointmentRepository,
            userRoleRepo: IUserRoleRepository
    ): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            val newAppointmentTime = endTime.time - startTime.time
            val totalTime = appointmentRepo.findTotalScheduledAppointmentTimeForUser(userId) ?: 0
            val theUserRole = userRoleRepo.findMembershipRoleByUserId(userId)

            if (theUserRole == null) {
                errors.put(com.radiotelescope.contracts.appointment.ErrorTag.CATEGORY_OF_SERVICE, "User's Category of Service has not yet been approved")
                return errors
            }

            when (theUserRole.role) {
                // Guest -> 5 hours
                com.radiotelescope.repository.role.UserRole.Role.GUEST -> {
                    if ((totalTime + newAppointmentTime) > com.radiotelescope.repository.appointment.Appointment.GUEST_APPOINTMENT_TIME_CAP)
                        errors.put(com.radiotelescope.contracts.appointment.ErrorTag.ALLOTTED_TIME, "You may only have up to 5 hours of observation time as a Guest")
                }
                // Everyone else -> 50 hours
                else -> {
                    if ((totalTime + newAppointmentTime) > com.radiotelescope.repository.appointment.Appointment.OTHER_USERS_APPOINTMENT_TIME_CAP)
                        errors.put(com.radiotelescope.contracts.appointment.ErrorTag.ALLOTTED_TIME, "Max allotted observation time is 50 hours at any given time")
                }
            }
        }

        return errors
    }


}