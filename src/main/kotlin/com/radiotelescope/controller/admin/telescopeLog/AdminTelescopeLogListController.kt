package com.radiotelescope.controller.admin.telescopeLog

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.telescopeLog.AdminTelescopeLogWrapper
import com.radiotelescope.contracts.telescopeLog.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle retrieving a Page of telescope logs
 *
 * @param telescopeLogWrapper the [AdminTelescopeLogWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminTelescopeLogListController(
        private val telescopeLogWrapper: AdminTelescopeLogWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of, given the page parameters are valid,
     * calling the [AdminTelescopeLogWrapper.list] method and responding to the
     * client based on if the user wsa authenticated, the command was executed
     * and was a success, or the command was executed and was a failure
     *
     * @param pageNumber the page number
     * @param pageSize the page size
     * @return a [Result] object
     */
    @GetMapping(value = ["/api/telescopeLogs"])
    fun execute(@RequestParam("pageNumber") pageNumber: Int,
                @RequestParam("pageSize") pageSize: Int): Result {
        if (pageNumber < 0 || pageSize <= 0) {
            val errors = pageErrors()

            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.TELESCOPE_LOG,
                            action = "Telescope Log List Retrieval",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        } else {
            val sort = Sort(Sort.Direction.DESC, "id")
            val pageRequest = PageRequest.of(pageNumber, pageSize, sort)

            telescopeLogWrapper.list(
                    pageable = pageRequest
            ) {
                // NOTE: This command currently only has a success scenario
                // (given the user is authenticated)
                it.success?.let { info ->
                    result = Result(data = info)
                }
            }?.let {
                // If we get here, this means the user did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.TELESCOPE_LOG,
                                action = "Telescope Log List Retrieval",
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
    private fun pageErrors(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}