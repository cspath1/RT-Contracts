package com.radiotelescope.contracts.feedback

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.feedback.IFeedbackRepository

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
}