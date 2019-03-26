package com.radiotelescope.contracts.appointment.wrapper

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.factory.manual.ManualAppointmentFactory
import com.radiotelescope.contracts.appointment.manual.AddFreeControlAppointmentCommand
import com.radiotelescope.contracts.appointment.manual.StartFreeControlAppointment
import com.radiotelescope.contracts.appointment.manual.StopFreeControlAppointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [ManualAppointmentFactory] and is responsible for
 * all user role validations for Manual Appointment Commands.
 *
 * @property context the [UserContext] interface
 * @property factory the [ManualAppointmentFactory] factory interface
 * @property appointmentRepo the [IAppointmentRepository] interface
 */
class UserManualAppointmentWrapper(
        private val context: UserContext,
        private val factory: ManualAppointmentFactory,
        appointmentRepo: IAppointmentRepository,
        viewerRepo: IViewerRepository
) : BaseUserAppointmentWrapper(
        context = context,
        factory = factory,
        appointmentRepo = appointmentRepo,
        viewerRepo = viewerRepo
) {
    /**
     * Wrapper method for the [ManualAppointmentFactory.startAppointment] method that adds
     * Spring Security authorization to the [StartFreeControlAppointment] command object.
     *
     * @param request the [StartFreeControlAppointment.Request] object
     * @return an [AccessReport] if authorization fails, null otherwise
     */
    fun startAppointment(request: StartFreeControlAppointment.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.startAppointment(request)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [ManualAppointmentFactory.addCommand] method that adds
     * Spring Security authorization to the [AddFreeControlAppointmentCommand] command object.
     *
     * @param request the [AddFreeControlAppointmentCommand.Request] object
     * @return an [AccessReport] if authorization fails, null otherwise
     */
    fun addCommand(request: AddFreeControlAppointmentCommand.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.addCommand(request)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [ManualAppointmentFactory.stopAppointment] method that adds
     * Spring Security authorization to the [StopFreeControlAppointment] command object.
     *
     * @param appointmentId the Appointment id
     * @return an [AccessReport] if authorization fails, null otherwise
     */
    fun stopAppointment(appointmentId: Long, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.stopAppointment(appointmentId)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }
}