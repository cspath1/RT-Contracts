package com.radiotelescope.controller.admin.log

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.log.AdminLogWrapper
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle retrieving a [Page] of logs
 *
 * @param logWrapper the [AdminLogWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminLogListController(
        private val logWrapper: AdminLogWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of, given the page parameters are valid,
     * calling the [AdminLogWrapper.list] method and responding to the client based
     * on if the user was authenticated, the command was executed and was a success,
     * or the command was executed and was a failure
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/logs"])
    fun execute(@RequestParam("pageNumber") pageNumber: Int?,
                @RequestParam("pageSize") pageSize: Int?): Result {
        // If any of the request params are null, respond with errors
        if ((pageNumber == null || pageNumber < 0) || (pageSize == null || pageSize <= 0)) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.LOG,
                            action = "Log List Retrieval",
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        }
        // Otherwise call the wrapper method
        else {
            val sort = Sort(Sort.Direction.DESC, "id")
            val pageRequest = PageRequest.of(pageNumber, pageSize, sort)
            logWrapper.list(
                    pageable = pageRequest
            ) { it ->
                // NOTE: This command currently only has a success scenario
                // (given the user is authenticated)
                // If the command was a success
                it.success?.let {
                    result = Result(data = it)
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.LOG,
                                action = "Log List Retrieval",
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