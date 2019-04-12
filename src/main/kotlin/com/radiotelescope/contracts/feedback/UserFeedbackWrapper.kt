package com.radiotelescope.contracts.feedback

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

/**
 * Wrapper that takes a [FeedbackFactory] and is responsible for all
 * user role validations for the Feedback Entity
 *
 * @param factory the [FeedbackFactory] interface
 */
class UserFeedbackWrapper(
        private val factory: FeedbackFactory
) {
    /**
     * Wrapper method for the [FeedbackFactory.create] method.
     *
     * @param request the [Create.Request] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request)
    }
}