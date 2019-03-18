package com.radiotelescope.contracts.feedback

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.feedback.Feedback
import com.radiotelescope.repository.feedback.IFeedbackRepository

/**
 * Override of the [Command] interface used for Feedback creation
 *
 * @param request the [Request] object
 * @param feedbackRepo the [IFeedbackRepository] interface
 */
class Create(
        private val request: Request,
        private val feedbackRepo: IFeedbackRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [Feedback] object and return
     * the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theFeedback = request.toEntity()
            feedbackRepo.save(theFeedback)
            return SimpleResult(theFeedback.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * [Request] object. It will ensure that the priority is between 1 and 10
     * (inclusive), and that the comments field is not blank.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (priority < 1 || priority > 10)
                errors.put(ErrorTag.PRIORITY, "Priority must be between 1 and 10 (inclusive)")
            if (comments.isBlank())
                errors.put(ErrorTag.COMMENTS, "Required field")
            if (name != null && name.length > 100)
                errors.put(ErrorTag.NAME, "Name must be under 100 characters")
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for feedback creation.
     * Implements the [BaseCreateRequest] interface
     */
    data class Request(
            val name: String?,
            val priority: Int,
            val comments: String
    ) : BaseCreateRequest<Feedback> {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] method
         * that returns a [Feedback] object
         */
        override fun toEntity(): Feedback {
            return Feedback(
                    name = name,
                    priority = priority,
                    comments = comments
            )
        }
    }
}