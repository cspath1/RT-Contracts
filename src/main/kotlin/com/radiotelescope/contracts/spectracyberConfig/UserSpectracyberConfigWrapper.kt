package com.radiotelescope.contracts.spectracyberConfig

import com.google.common.collect.Multimap
import com.radiotelescope.security.UserContext
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport

/**
 * Wrapper that takes a [SpectracyberConfigFactory] and is responsible for all
 * user role validations for the SpectracyberConfig Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [SpectracyberConfigFactory] interface
 */
class UserSpectracyberConfigWrapper (
        private val context: UserContext,
        private val factory: SpectracyberConfigFactory,
        private val userRepo: IUserRepository,
        private val appointmentRepo: IAppointmentRepository
) {
    /**
     * Wrapper method for the [SpectracyberConfigFactory.update] method.
     *
     * @param userId the user id associated with the appointment that is the parent of the record
     * @param request the [Update.Request] object
     * @return a [Command] object
     */
    fun update(userId: Long, request: Update.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            val theUser = userRepo.findById(context.currentUserId()!!)
            val theSpectracyberConfig = appointmentRepo.findAppointmentBySpectracyberConfigId(request.id).spectracyberConfig

            // If the user exists, they must have the same id as the to-be-updated record
            // or they must be an admin
            if (theUser.isPresent) {
                return if (theUser.get().id == userId) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.update(request)
                    ).execute(withAccess)
                } else {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.update(request)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [SpectracyberConfigFactory.retrieve] method.
     *
     * @param userId the user id associated with the appointment that is the parent of the record
     * @param spectracyberConfigId the spectracyber config id to retrieve
     * @return a [Command] object
     */
    fun retrieve(userId: Long, spectracyberConfigId: Long, withAccess: (result: SimpleResult<SpectracyberConfig, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        // If the user is logged in
        if (context.currentUserId() != null) {
            val theUser = userRepo.findById(context.currentUserId()!!)

            // If the user exists, they must have the same id as the to-be-retrieved record
            // or they must be an admin
            if (theUser.isPresent) {
                return if (theUser.get().id == userId) {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.USER),
                            successCommand = factory.retrieve(spectracyberConfigId)
                    ).execute(withAccess)
                } else {
                    context.require(
                            requiredRoles = listOf(UserRole.Role.ADMIN),
                            successCommand = factory.retrieve(spectracyberConfigId)
                    ).execute(withAccess)
                }
            }
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }
}