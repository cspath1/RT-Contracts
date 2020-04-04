package com.radiotelescope.contracts.frontpagePicture

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.security.UserContext
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport

/**
 * Wrapper that takes a [FrontpagePictureFactory] and is responsible for all
 * user role validations for the [FrontpagePicture] Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [FrontpagePictureFactory] factory interface
 */
class UserFrontpagePictureWrapper (
        private val context: UserContext,
        private val factory: FrontpagePictureFactory
) {
    /**
     * Wrapper method for the [FrontpagePictureFactory.submit] method.
     *
     * @param request the [Submit.Request] form
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun submit(request: Submit.Request, withAccess: (result: SimpleResult<FrontpagePicture, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.USER),
                successCommand = factory.submit(request)
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [FrontpagePictureFactory.approveDeny] method.
     *
     * @param request the [ApproveDeny.Request] form
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun approveDeny(request: ApproveDeny.Request, withAccess: (result: SimpleResult<FrontpagePicture, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.approveDeny(request)
        ).execute(withAccess)
    }
}