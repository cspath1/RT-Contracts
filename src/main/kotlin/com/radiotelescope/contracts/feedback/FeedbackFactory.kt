package com.radiotelescope.contracts.feedback

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.feedback.Feedback
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

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

    /**
     * Abstract command used to retrieve all [Feedback] objects
     *
     * @param pageable the pageable object
     * @return a [Command] object
     */
    fun list(pageable: Pageable): Command<Page<Feedback>, Multimap<ErrorTag, String>>
}