package com.radiotelescope.contracts.feedback

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.feedback.Feedback

/**
 * Abstract factory interface with methods for all [Feedback] operations
 */
interface FeedbackFactory {
    /**
     * Abstract command used to create a new [Feedback] object
     *
     * @param request the [Create.Request] request
     * @return a [Command] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>
}