package com.radiotelescope.controller.admin.user

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.contracts.user.List
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller to handle retrieving a [Page] of users
 *
 * @param userWrapper the [UserUserWrapper] interface
 * @param logger the [Logger] service
 */
@RestController
class AdminUserListController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of using the [pageNumber]
     * and [pageSize] request parameters to create a [PageRequest]
     * so the [UserUserWrapper.pageable] method can be called.
     *
     * If this method returns an [AccessReport], this means the user
     * accessing the endpoint did not pass authentication.
     *
     * Otherwise, the [List] command was executed, and the controller
     * should respond based on whether the command was a success or
     * failure
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/users"])
    fun execute(@RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?): Result {
        // If any of the request params are null, respond with errors
        if ((pageNumber == null || pageNumber < 0) || (pageSize == null || pageSize <= 0)) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User LogList Retrieval",
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val sort = Sort(Sort.Direction.DESC, "id")
            val pageRequest = PageRequest.of(pageNumber, pageSize, sort)
            userWrapper.list(pageRequest) { it ->
                // If the command was a success
                it.success?.let { page ->
                    // Create success logs
                    page.content.forEach {
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        affectedTable = Log.AffectedTable.USER,
                                        action = "User LogList Retrieval",
                                        affectedRecordId = it.id
                                )
                        )
                    }

                    result = Result(data = page)
                }
                // If the command was a failure
                it.error?.let { errors ->
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.USER,
                                    action = "User LogList Retrieval",
                                    affectedRecordId = null
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User LogList Retrieval",
                                affectedRecordId = null
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