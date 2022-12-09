package com.radiotelescope.contracts.feedback

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.feedback.Feedback
import com.radiotelescope.repository.feedback.IFeedbackRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation of the [FeedbackFactory] interface
 *
 * @param feedbackRepo the [IFeedbackRepository] interface
 */
class BaseFeedbackFactory(
        private val feedbackRepo: IFeedbackRepository
) : FeedbackFactory {
    /**
     * Override of the [FeedbackFactory.create] method that will return a [Create] command
     *
     * @param request the [Create.Request] object
     * @return a [Create] command object
     */
    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Create(
                request = request,
                feedbackRepo = feedbackRepo
        )
    }

    /**
     * Override of the [FeedbackFactory.list] method that will return a [List] command
     *
     * @param pageable the pageable object
     * @return a [Create] command object
     */
    override fun list(pageable: Pageable): Command<Page<Feedback>, Multimap<ErrorTag, String>> {
        return List(
                pageable = pageable,
                feedbackRepo = feedbackRepo
        )
    }
}