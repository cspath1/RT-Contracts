package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [AppointmentFactory] and is responsible for all
 * user role validations for endpoints for the Appointment Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [AppointmentFactory] factory interface
 */
class UserAppointmentWrapper(
        private val context: UserContext,
        private val factory: AppointmentFactory,
        private val appointmentRepo: IAppointmentRepository
) {
    /**
     * Wrapper method for the [AppointmentFactory.create] method that adds Spring
     * Security authentication to the [Create] command object.
     *
     * @param request the [Create.Request] object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun create(request: Create.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER),
                successCommand = factory.create(request)
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [AppointmentFactory.retrieve] method that adds Spring
     * Security retrieve to the [Retrieve] command object.
     *
     * @param id the Appointment id
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(id: Long, withAccess: (result: SimpleResult<AppointmentInfo, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        val theAppointment = appointmentRepo.findById(id).get()
        if (context.currentUserId() != null &&
                context.currentUserId() == theAppointment.user!!.id) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.USER),
                    successCommand = factory.retrieve(id)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER))
    }
}