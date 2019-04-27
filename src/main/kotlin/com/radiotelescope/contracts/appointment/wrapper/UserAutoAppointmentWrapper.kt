package com.radiotelescope.contracts.appointment.wrapper

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.create.AppointmentCreate
import com.radiotelescope.contracts.appointment.factory.AppointmentFactory
import com.radiotelescope.contracts.appointment.factory.auto.AutoAppointmentFactory
import com.radiotelescope.contracts.appointment.request.AppointmentRequest
import com.radiotelescope.contracts.appointment.request.CoordinateAppointmentRequest
import com.radiotelescope.contracts.appointment.update.AppointmentUpdate
import com.radiotelescope.contracts.appointment.update.CoordinateAppointmentUpdate
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes an [AppointmentFactory] and is responsible for all
 * user role validations for endpoints for the Appointment Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [AppointmentFactory] factory interface
 * @property appointmentRepo the [IAppointmentRepository] interface
 */
class UserAutoAppointmentWrapper(
        private val context: UserContext,
        private val factory: AutoAppointmentFactory,
        private val appointmentRepo: IAppointmentRepository,
        viewerRepo: IViewerRepository
) : BaseUserAppointmentWrapper(
        context = context,
        factory = factory,
        appointmentRepo = appointmentRepo,
        viewerRepo = viewerRepo
) {
    /**
     * Wrapper method for the [AutoAppointmentFactory.create] method that adds Spring
     * Security authentication to the [AppointmentCreate] command object.
     *
     * @param request the [AppointmentCreate.Request] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun create(request: AppointmentCreate.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null && context.currentUserId() == request.userId) {
            // If public and primary, they only need to be a base user
            return if (request.isPublic && request.priority == Appointment.Priority.PRIMARY)
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.create(request)
                ).execute(withAccess)
            // If the priority is primary (but not public), they need to be a researcher or admin
            else if (request.priority == Appointment.Priority.PRIMARY)
                context.requireAny(
                        requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.RESEARCHER),
                        successCommand = factory.create(request)
                ).execute(withAccess)
            // Otherwise, they need to be an admin
            else
                context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.create(request)
                ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AutoAppointmentFactory.update] method that adds Spring
     * Security authentication to the [CoordinateAppointmentUpdate] command object.
     *
     * @param request the user Id of the appointment
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun update(request: AppointmentUpdate.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (!appointmentRepo.existsById(request.id)) {
            return AccessReport(missingRoles = null, invalidResourceId = invalidAppointmentIdErrors(request.id))
        }

        val theAppointment = appointmentRepo.findById(request.id).get()

        if(context.currentUserId() != null) {
            if (context.currentUserId() == theAppointment.user.id) {
                // If public and primary, they only need to be a base user
                return if (request.isPublic && request.priority == Appointment.Priority.PRIMARY)
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.update(
                                    request = request
                            )
                    ).execute(withAccess)
                // If it's primary, but not public, they need to be a researcher
                else if (request.priority == Appointment.Priority.PRIMARY)
                    context.requireAny(
                            requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.RESEARCHER),
                            successCommand = factory.update(
                                    request = request
                            )
                    ).execute(withAccess)
                // Otherwise, they must be an admin
                else
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.update(
                                    request = request
                            )
                    ).execute(withAccess)
            }
            // Otherwise, they need to be an admin
            else {
                return context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.update(
                                request = request
                        )
                ).execute(withAccess)
            }
        }
        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AutoAppointmentFactory.request] method that adds Spring
     * Security authentication to the [CoordinateAppointmentRequest] command object.
     *
     * @param request the [CoordinateAppointmentRequest.Request] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun request(request: AppointmentRequest.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null && context.currentUserId() == request.userId) {
            // If public and primary, they only need to be a base user
            return if (request.isPublic && request.priority == Appointment.Priority.PRIMARY)
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.request(request)
                ).execute(withAccess)
            // If primary, but not public, they need to be a researcher
            else if (request.priority == Appointment.Priority.PRIMARY)
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                        successCommand = factory.request(request)
                ).execute(withAccess)
            // Otherwise, they must be an admin
            else
                context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.request(request)
                ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }
}