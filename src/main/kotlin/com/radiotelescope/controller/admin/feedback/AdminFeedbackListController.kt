package com.radiotelescope.controller.admin.feedback

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.feedback.UserFeedbackWrapper
import com.radiotelescope.contracts.feedback.ErrorTag
import com.radiotelescope.contracts.feedback.List
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle retrieving all Feedback submissions
 *
 * @param feedbackWrapper the [UserFeedbackWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminFeedbackListController(
        private val feedbackWrapper: UserFeedbackWrapper,
        logger: Logger
): BaseRestController(logger) {
    /**
     * Execute method that is in charge of using the [pageNumber]
     * and [pageSize] request parameters to create a [PageRequest]
     * so the [UserFeedbackWrapper.list] method can be called.
     *
     * If this method returns an [AccessReport], this means the user
     * accessing the endpoint did not pass authentication.
     *
     * Otherwise, the [List] command was executed, and the controller
     * should respond based on whether the command was a success or
     * failure
     */
    @GetMapping(value = ["/api/feedback"])
    fun execute(@RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?) : Result {
        // If any of the request params are null, respond with errors
        if ((pageNumber == null || pageNumber < 0) || (pageSize == null || pageSize <= 0)) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.FEEDBACK,
                            action = "Feedback List Retrieval",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val sort = Sort(Sort.Direction.DESC, "id")
            val pageRequest = PageRequest.of(pageNumber, pageSize, sort)
            feedbackWrapper.list(pageRequest) {
                // NOTE: This command currently only has a success scenario
                // (given the user is authenticated)
                // If the command was a success
                it.success?.let { page ->
                    // Create success logs
                    page.content.forEach { info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        affectedTable = Log.AffectedTable.FEEDBACK,
                                        action = "Feedback List Retrieval",
                                        affectedRecordId = info.id,
                                        status = HttpStatus.OK.value()
                                )
                        )
                    }

                    result = Result(data = page)
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.FEEDBACK,
                                action = "Feedback List Retrieval",
                                affectedRecordId = null,
                                status = HttpStatus.FORBIDDEN.value()
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }

    /**
     * Private method to return a [HashMultimap] of errors in the event
     * that the page size and page number are invalid
     */
    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}