package com.radiotelescope.contracts.feedback

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.feedback.Feedback
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper that takes a [FeedbackFactory] and is responsible for all
 * user role validations for the Feedback Entity
 *
 * @param factory the [FeedbackFactory] interface
 */
class UserFeedbackWrapper(
        private val context: UserContext,
        private val factory: FeedbackFactory
) {
    /**
     * Wrapper method for the [FeedbackFactory.create] method.
     *
     * @param request the [Create.Request] object
     * @return a [Command] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request)
    }

    /**
     * Wrapper method for the [FeedbackFactory.list] method.
     *
     * @param pageable the pageable object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun list(pageable: Pageable, withAccess: (result: SimpleResult<Page<Feedback>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.list(pageable)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER, UserRole.Role.ADMIN), invalidResourceId = null)
    }
}