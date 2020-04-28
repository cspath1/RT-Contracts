package com.radiotelescope.contracts.feedback

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.feedback.Feedback
import com.radiotelescope.repository.feedback.IFeedbackRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface method used for Feedback List retrieval
 *
 * @param pageable the [Pageable] interface
 * @param feedbackRepo the [IFeedbackRepository] interface
 */
class List(
        private val pageable: Pageable,
        private val feedbackRepo: IFeedbackRepository
) : Command<Page<Feedback>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that calls the [IFeedbackRepository.findAll]
     * method, passes in the pageable parameter, and returns the generated page.
     */
    override fun execute(): SimpleResult<Page<Feedback>, Multimap<ErrorTag, String>> {
        val feedbackPage = feedbackRepo.findAll(pageable)

        return SimpleResult(feedbackPage, null)
    }
}