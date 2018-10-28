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

@RestController
class AdminLogErrorListController(
        private val logWrapper: AdminLogWrapper,
        logger: Logger
) : BaseRestController(logger) {
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/logs/{logId}/errors"])
    fun execute(@PathVariable("logId") logId: Long): Result {
        logWrapper.retrieveErrors(
                logId = logId
        ) { it ->
            // If the command was a success
            it.success?.let {
                result = Result(data = it)
            }
            // If the command was a failure
            it.error?.let {
                result = Result(errors = it.toStringMap())
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.LOG,
                            action = "Log Error List Retrieval",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}