package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.model.appointment.SearchCriteria
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.toStringMap
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper that takes an [AppointmentFactory] and is responsible for all
 * user role validations for endpoints for the Appointment Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [AppointmentFactory] factory interface
 * @property appointmentRepo the [IAppointmentRepository] interface
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
        if (context.currentUserId() != null && context.currentUserId() == request.userId) {
            // If public, they only need to be a base user
            return if (request.isPublic)
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.create(request)
                ).execute(withAccess)
            // Otherwise, they need to be a researcher or admin
            else
                context.requireAny(
                        requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.RESEARCHER),
                        successCommand = factory.create(request)
                ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.retrieve] method that adds Spring
     * Security retrieve to the [Retrieve] command object.
     *
     * @param id the Appointment id
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(id: Long, withAccess: (result: SimpleResult<AppointmentInfo, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (!appointmentRepo.existsById(id)) {
            return AccessReport(missingRoles = null, invalidResourceId = invalidAppointmentIdErrors(id))
        }

        val theAppointment = appointmentRepo.findById(id).get()

        if (context.currentUserId() != null &&
                context.currentUserId() == theAppointment.user.id) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.USER),
                    successCommand = factory.retrieve(id)
            ).execute(withAccess)
        } else if (theAppointment.isPublic) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.USER),
                    successCommand = factory.retrieve(id)
            ).execute(withAccess)
        } else {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.retrieve(id)
            ).execute(withAccess)
        }
    }

    /**
     * Wrapper method for the [AppointmentFactory.userFutureList] method that adds Spring
     * Security authentication to the [UserFutureList] command object.
     *
     * @param userId the user Id of the appointment
     * @param pageable contains the pageSize and pageNumber
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun userFutureList(userId: Long, pageable: Pageable, withAccess: (result: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if(context.currentUserId() != null) {
            if (context.currentUserId() == userId) {
                return context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.userFutureList(
                                userId = userId,
                                pageable = pageable
                        )
                ).execute(withAccess)
            }
            // Otherwise, they need to be an admin
            else {
                return context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.userFutureList(
                                userId = userId,
                                pageable = pageable
                        )
                ).execute(withAccess)
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.userCompletedList] method that adds Spring
     * Security authentication to the [UserCompletedList] command object
     *
     * @param userId the User id
     * @param pageable the [Pageable] interface
     * @return an [AccessReport] if authentication fails, null otherwise
     */
    fun userCompleteList(userId: Long, pageable: Pageable, withAccess: (result: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return if (context.currentUserId() == userId) {
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.userCompletedList(
                                userId = userId,
                                pageable = pageable
                        )
                ).execute(withAccess)
            } else {
                context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.userCompletedList(
                                userId = userId,
                                pageable = pageable
                        )
                ).execute(withAccess)
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.cancel] method that adds Spring Security
     * authentication to the [Cancel] command object
     *
     * @param appointmentId the Appointment id
     * @return an [AccessReport] if authentication fails, null otherwise
     */
    fun cancel(appointmentId: Long, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (!appointmentRepo.existsById(appointmentId)) {
            return AccessReport(missingRoles = null, invalidResourceId = invalidAppointmentIdErrors(appointmentId))
        }

        val theAppointment = appointmentRepo.findById(appointmentId).get()

        if (context.currentUserId() != null) {
            return if (context.currentUserId() == theAppointment.user.id) {
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.cancel(
                                appointmentId = appointmentId
                        )
                ).execute(withAccess)
            } else {
                context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.cancel(
                                appointmentId = appointmentId
                        )
                ).execute(withAccess)
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.retrieveFutureAppointmentsByTelescopeId] method that
     * adds Spring Security authentication to the [RetrieveFutureAppointmentsByTelescopeId] command object
     *
     * @param telescopeId the Telescope id
     * @param pageable the [Pageable] interface
     * @return an [AccessReport] if authentication fails, null otherwise
     */
    fun retrieveFutureAppointmentsByTelescopeId(telescopeId: Long, pageable: Pageable, withAccess: (result: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER),
                successCommand = factory.retrieveFutureAppointmentsByTelescopeId(
                        telescopeId = telescopeId,
                        pageable = pageable
                )
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [AppointmentFactory.update] method that adds Spring
     * Security authentication to the [Update] command object.
     *
     * @param request the user Id of the appointment
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun update(request: Update.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport?{
        if (!appointmentRepo.existsById(request.id)) {
            return AccessReport(missingRoles = null, invalidResourceId = invalidAppointmentIdErrors(request.id))
        }

        val theAppointment = appointmentRepo.findById(request.id).get()

        if(context.currentUserId() != null) {
            if (context.currentUserId() == theAppointment.user.id) {
                // If public, they only need to be a base user
                return if (request.isPublic)
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.update(
                                    request = request
                            )
                    ).execute(withAccess)
                // Otherwise, they need to be a researcher
                else
                    context.requireAny(
                            requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.RESEARCHER),
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
     * Wrapper method for the [AppointmentFactory.makePublic] method that adds Spring
     * Security authentication to the [makePublic] command object.
     *
     * @param appointmentId the Appointment's Id
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun makePublic(appointmentId: Long, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (!appointmentRepo.existsById(appointmentId)) {
            return AccessReport(missingRoles = null, invalidResourceId = invalidAppointmentIdErrors(appointmentId))
        }

        val theAppointment = appointmentRepo.findById(appointmentId).get()

        if(context.currentUserId() != null) {
            return if (context.currentUserId() == theAppointment.user.id) {
                context.require(
                        requiredRoles = listOf(UserRole.Role.RESEARCHER),
                        successCommand = factory.makePublic(
                                appointmentId = appointmentId
                        )
                ).execute(withAccess)
            } else {
                context.require(
                        requiredRoles = listOf(UserRole.Role.ADMIN),
                        successCommand = factory.makePublic(
                                appointmentId = appointmentId
                        )
                ).execute(withAccess)
            }

        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.listBetweenDates] method that adds Spring
     * Security authentication to the [ListBetweenDates] command object.
     *
     * @param request the [ListBetweenDates.Request] object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun listBetweenDates(request: ListBetweenDates.Request, withAccess: (result: SimpleResult<List<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER),
                successCommand = factory.listBetweenDates(
                        request = request
                )
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [AppointmentFactory.publicCompletedAppointments] method that adds
     * Spring Security authentication to the [PublicCompletedAppointments] command object.
     *
     * @param pageable the Pageable interface
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun publicCompletedAppointments(pageable: Pageable, withAccess: (result: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER),
                successCommand = factory.publicCompletedAppointments(
                        pageable = pageable
                )
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [AppointmentFactory.request] method that adds Spring
     * Security authentication to the [Request] command object.
     *
     * @param request the [Request.Request] object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun request(request: Request.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null && context.currentUserId() == request.userId) {
            // If public, they only need to be a base user
            return if (request.isPublic)
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER),
                        successCommand = factory.request(request)
                ).execute(withAccess)
            // Otherwise, they need to be a researcher
            else
                context.require(
                        requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                        successCommand = factory.request(request)
                ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.listRequest] method that adds Spring
     * Security authentication to the [RequestedList] command object.
     *
     * @param pageable contains the pageSize and pageNumber
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun requestedList(pageable: Pageable, withAccess: (result: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if(context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.requestedList(
                            pageable = pageable
                    )
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.approveDenyRequest] method that adds Spring
     * Security authentication to the [ApproveDenyRequest] command object.
     *
     * @param request the [ApproveDenyRequest.Request]
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun approveDenyRequest(request: ApproveDenyRequest.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if(context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.approveDenyRequest(
                            request = request
                    )
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.userAvailableTime] method that adds Spring
     * Security authentication to the [UserAvailableTime] command object.
     *
     * @param userId the User's Id
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun userAvailableTime(userId: Long, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return if (context.currentUserId() != null && context.currentUserId() == userId)
            context.require(
                    requiredRoles = listOf(UserRole.Role.USER),
                    successCommand = factory.userAvailableTime(
                            userId = userId
                    )
            ).execute(withAccess)
        else
            AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [AppointmentFactory.search] method that adds Spring
     * Security authentication to the [Search] command object
     *
     * @param searchCriteria a [List] of [SearchCriteria]
     * @param pageable the [Pageable] interface
     * @return an [AccessReport] if authentication fails, null otherwise
     */
    fun search(searchCriteria: List<SearchCriteria>, pageable: Pageable, withAccess: (result: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(),
                    successCommand = factory.search(searchCriteria, pageable)
            ).execute(withAccess)
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
        errors.put(ErrorTag.ID, "Appointment #$id could not be found")
        return errors.toStringMap()
    }
}