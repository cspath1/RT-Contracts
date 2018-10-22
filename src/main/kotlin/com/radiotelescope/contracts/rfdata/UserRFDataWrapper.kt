package com.radiotelescope.contracts.rfdata

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [RFDataFactory] and is responsible for
 * all user role validations for the RFData Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [RFDataFactory] factory interface
 * @property appointmentRepo the [IAppointmentRepository] interface
 */
class UserRFDataWrapper(
        private val context: UserContext,
        private val factory: RFDataFactory,
        private val appointmentRepo: IAppointmentRepository
) {
    /**
     * Wrapper method for the [RFDataFactory.retrieveAppointmentData] method that adds
     * Spring Security authentication to the [RetrieveAppointmentData] command object.
     *
     * @param appointmentId the Appointment id
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieveAppointmentData(appointmentId: Long, withAccess: (result: SimpleResult<List<RFDataInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user logged in
        if (context.currentUserId() != null) {
            val theAppointment = appointmentRepo.findById(appointmentId).get()
            // If the user id matches the appointment's user id
            return if (context.currentUserId() == theAppointment.user!!.id)
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.retrieveAppointmentData(appointmentId)
                ).execute(withAccess)
            else {
                // Otherwise, if the appointment is public, anyone
                // can view it
                return if (theAppointment.isPublic) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.retrieveAppointmentData(appointmentId)
                    ).execute(withAccess)
                }
                // If not, they must be an admin
                else {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.retrieveAppointmentData(appointmentId)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER))
    }
}