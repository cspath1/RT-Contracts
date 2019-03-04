package com.radiotelescope.controller.admin.log

import com.radiotelescope.contracts.log.AdminLogWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller used to retrieve a list of errors for an error Log
 *
 * @param logWrapper the [AdminLogWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminLogErrorListController(
        private val logWrapper: AdminLogWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that takes the logId [PathVariable] and
     * calls the [AdminLogWrapper.retrieveErrors] method. It will
     * then respond to the client based on if the user was authenticated,
     * the command was executed and was a success, or the command was
     * executed and it was a failure
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/logs/{logId}/errors"])
    fun execute(@PathVariable("logId") logId: Long): Result {
        logWrapper.retrieveErrors(
                logId = logId
        ) {
            // If the command was a success
            it.success?.let { info ->
                result = Result(data = info)
            }
            // If the command was a failure
            it.error?.let { error ->
                result = Result(errors = error.toStringMap())
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.LOG,
                            action = "Log Error List Retrieval",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}