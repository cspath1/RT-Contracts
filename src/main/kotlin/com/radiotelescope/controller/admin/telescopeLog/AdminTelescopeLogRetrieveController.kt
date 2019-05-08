package com.radiotelescope.controller.admin.telescopeLog

import com.radiotelescope.contracts.telescopeLog.AdminTelescopeLogWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller used to retrieve a specific control room log file
 *
 * @param telescopeLogWrapper the [AdminTelescopeLogWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminTelescopeLogRetrieveController(
        private val telescopeLogWrapper: AdminTelescopeLogWrapper,
        logger: Logger
) :  BaseRestController(logger) {
    /**
     * Execute method that takes the telescope log id path variable and
     * calls the [AdminTelescopeLogWrapper.retrieve] method. It will then respond
     * to the client based on if the user was authenticated, the command was executed and
     * was a success, or the command ws executed and was failure.
     *
     * @param telescopeLogId the Telescope Log id
     * @return a [Result] object
     */
    @GetMapping(value = ["/api/telescopeLogs/{telescopeLogId}"])
    fun execute(@PathVariable(value = "telescopeLogId") telescopeLogId: Long): Result {
        telescopeLogWrapper.retrieve(
                id = telescopeLogId
        ) {
            // If the command was a success
            it.success?.let { info ->
                result = Result(data = info)
            }
            // If the command was a failure
            it.error?.let { errors ->
                result = Result(errors = errors.toStringMap())
            }
        }?.let {
            // If we get here, this means the user did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.TELESCOPE_LOG,
                            action = "Telescope Log Retrieval",
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