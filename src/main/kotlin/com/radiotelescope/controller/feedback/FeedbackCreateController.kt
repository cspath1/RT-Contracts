package com.radiotelescope.controller.feedback

import com.radiotelescope.contracts.feedback.UserFeedbackWrapper
import com.radiotelescope.contracts.feedback.Create
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.feedback.CreateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle Feedback submission
 *
 * @param feedbackWrapper the [UserFeedbackWrapper]
 * @param logger the [Logger] service
 */
@RestController
class FeedbackCreateController(
        private val feedbackWrapper: UserFeedbackWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [CreateForm]
     * into a [Create.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserFeedbackWrapper.create] method.
     * Based on the result of this call, the method will either respond with
     * the data or the errors from the method call.
     */
    @PostMapping(value = ["/api/feedback"])
    fun execute(@RequestBody form: CreateForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.FEEDBACK,
                            action = "Feedback Submission",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise, execute the wrapper command
        let {
            val response = feedbackWrapper.create(
                    request = form.toRequest()
            ).execute()
            // If the command was a success
            response.success?.let { data ->
                // Create success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.FEEDBACK,
                                action = "Feedback Submission",
                                affectedRecordId = data,
                                status = HttpStatus.OK.value()
                        )
                )
                result = Result(data = data)
            }
            // Otherwise, it was an error
            response.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.FEEDBACK,
                                action = "Feedback Submission",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }

        return result
    }
}