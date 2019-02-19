package com.radiotelescope.contracts.viewer

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.AppointmentFactory
import com.radiotelescope.contracts.appointment.AppointmentInfo
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.toStringMap
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper that takes a [ViewerFactory] and is responsible for all
 * user role validations for endpoints for the Viewer Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [AppointmentFactory] factory interface
 * @property appointmentRepo the [IAppointmentRepository] interface */
class UserViewerWrapper (
        private val context: UserContext,
        private val factory: ViewerFactory,
        private val appointmentRepo: IAppointmentRepository
){
    /**
     * Wrapper method for the [ViewerFactory.sharePrivateAppointment] method that adds Spring
     * Security authentication to the [SharePrivateAppointment] command object.
     *
     * @param request the [SharePrivateAppointment.Request] command object
     * @return an [AccessReport] if the authentication fails, null otherwise
     */
    fun sharePrivateAppointment(request: SharePrivateAppointment.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (!appointmentRepo.existsById(request.appointmentId)) {
            return AccessReport(missingRoles = null, invalidResourceId = invalidAppointmentIdErrors(request.appointmentId))
        }

        val theAppointment = appointmentRepo.findById(request.appointmentId).get()

        if (context.currentUserId() != null) {
            return if (context.currentUserId() == theAppointment.user!!.id) {
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                        successCommand = factory.sharePrivateAppointment(
                                request = request
                        )
                ).execute(withAccess)
            } else {
                context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.sharePrivateAppointment(
                                request = request
                        )
                ).execute(withAccess)
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [ViewerFactory.listSharedAppointment] method that adds Spring
     * Security authentication to the [ListSharedAppointment] command object.
     *
     * @param userId the User's Id
     * @param pageable the [Pageable] interface
     * @return an [AccessReport] if the authentication fails, null otherwise
     */
    fun listSharedAppointment(userId: Long, pageable: Pageable, withAccess: (result: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return if(context.currentUserId() == userId) {
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.listSharedAppointment(
                                userId = userId,
                                pageable = pageable
                        )
                ).execute(withAccess)
            } else {
                context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.listSharedAppointment(
                                userId = userId,
                                pageable = pageable
                        )
                ).execute(withAccess)
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [ViewerFactory.listSharedUser] method that adds Spring
     * Security authentication to the [ListSharedUser] command object.
     *
     * @param appointmentId the Appointment's Id
     * @param pageable the [Pageable] interface
     * @return an [AccessReport] if the authentication fails, null otherwise
     */
    fun listSharedUser(appointmentId: Long, pageable: Pageable, withAccess: (result: SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (!appointmentRepo.existsById(appointmentId)) {
            return AccessReport(missingRoles = null, invalidResourceId = invalidAppointmentIdErrors(appointmentId))
        }

        val theAppointment = appointmentRepo.findById(appointmentId).get()

        if (context.currentUserId() != null) {
            return if(context.currentUserId() == theAppointment.user!!.id) {
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.listSharedUser(
                                appointmentId = appointmentId,
                                pageable = pageable
                        )
                ).execute(withAccess)
            } else {
                context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.listSharedUser(
                                appointmentId = appointmentId,
                                pageable = pageable
                        )
                ).execute(withAccess)
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Private method to return a [Map] of errors when an appointment could not be found.
     * This is needed when we must check if the user is the owner of an appointment or not
     *
     * @param id the Appointment id
     * @return a [Map] of errors
     */
    private fun invalidAppointmentIdErrors(id: Long): Map<String, Collection<String>> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.APPOINTMENT_ID, "Appointment #$id could not be found")
        return errors.toStringMap()
    }
}