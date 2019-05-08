package com.radiotelescope.controller.admin.log

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.log.AdminLogWrapper
import com.radiotelescope.contracts.log.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.model.log.Filter
import com.radiotelescope.repository.model.log.SearchCriteria
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

/**
 * Rest Controller to handle dynamically searching for logs
 *
 * @param logWrapper the [AdminLogWrapper]
 * @param logger the [Logger] service
 */
class AdminLogSearchController(
        private val logWrapper: AdminLogWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the request parameters
     * and adapting them into the parameters required for the [AdminLogWrapper.search]
     *
     * Once the parameters have been adapted, it will call the method, and respond
     * according to if the request was successfully authenticated, or whether the
     * request was a success or an error
     *
     * @param pageNumber the page number
     * @param pageSize the page size
     * @param value the search value
     * @param search the search criteria string
     * @return a [Result] object
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/logs/search"])
    fun execute(@RequestParam(value = "page") pageNumber: Int,
                @RequestParam(value = "size") pageSize: Int,
                @RequestParam(value = "value") value: Any,
                @RequestParam(value = "search") search: String): Result {
        val filter = Filter.fromField(search)
        if (pageNumber < 0 || pageSize <= 0) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.LOG,
                            action = "Log Search",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        }
        // Check if the filter is valid or not
        else if (filter == null){
            val errors = filterErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.LOG,
                            action = "Log Search",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        }
        // Otherwise call the wrapper method
        else {
            val searchCriteria = SearchCriteria(filter, value)
            val pageable = PageRequest.of(pageNumber, pageSize)

            logWrapper.search(
                    searchCriteria = searchCriteria,
                    pageable = pageable
            ) {
                // If the command was a success
                it.success?.let { page ->
                    // Create success logs
                    page.forEach { info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        affectedTable = Log.AffectedTable.LOG,
                                        action = "Log Search",
                                        affectedRecordId = info.id,
                                        status = HttpStatus.OK.value()
                                )
                        )
                    }

                    result = Result(data = page)
                }
                // Otherwise, it was a failure
                it.error?.let { error ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.LOG,
                                    action = "LOG Search",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = error.toStringMap()
                    )

                    result = Result(
                            errors = error.toStringMap()
                    )
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.LOG,
                                action = "LOG Search",
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

    /**
     * Private method to return a [HashMultimap] of errors in the event
     * that the search/filter is invalid
     */
    private fun filterErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.SEARCH, "Invalid search filter")
        return errors
    }

}