package com.radiotelescope.contracts.allottedTimeCap

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.allottedTimeCap.AllottedTimeCap
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes an [AllottedTimeCapFactory] and is responsible for all
 * user role validations for endpoints for the AllottedTimeCap Entity
 *
 * @param context the [UserContext] interface
 * @param factory the [AllottedTimeCapFactory] factory interface
 */
class UserAllottedTimeCapWrapper(
        private val context: UserContext,
        private val factory: AllottedTimeCapFactory
) {

    /**
     * Wrapper method for the [AllottedTimeCapFactory.update] method that adds Spring
     * Security authentication to the [Update] command object.
     *
     * @param request the user Id of the appointment
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun update(request: Update.Request, withAccess: (result: SimpleResult<AllottedTimeCap, Multimap<ErrorTag, String>>) -> Unit): AccessReport?{
        if(context.currentUserId() != null){
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.update(request)
            ).execute(withAccess)
        }
        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }
}