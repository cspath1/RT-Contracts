package com.radiotelescope.controller.spectracyberConfig

import com.radiotelescope.contracts.spectracyberConfig.UserSpectracyberConfigWrapper
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
class SpectracyberConfigRetrieveController(
        private val spectracyberConfigWrapper: UserSpectracyberConfigWrapper,
        logger: Logger
) : BaseRestController(logger) {

    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/appointments/{spectracyberConfigId}/spectracyberConfig"])
    fun execute(@PathVariable("spectracyberConfigId") spectracyberConfigId: Long): Result {
        // Call the factory command
        spectracyberConfigWrapper.retrieve(
                spectracyberConfigId = spectracyberConfigId
        ) { response ->
            // If the command was a success
            response.success?.let { data ->
                result = Result(
                        data = data
                )

                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.SPECTRACYBER_CONFIG,
                                action = "Spectracyber Config Retrieve",
                                affectedRecordId = data.id,
                                status = HttpStatus.OK.value()
                        )
                )
            }
            // Otherwise, it was a failure
            response.error?.let { error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.SPECTRACYBER_CONFIG,
                                action = "Spectracyber Config Retrieve",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = error.toStringMap()
                )
                result = Result(
                        errors = error.toStringMap()
                )
            }
        }?.let { report ->
            // If we get here, that means the user was not authenticated
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.SPECTRACYBER_CONFIG,
                            action = "Spectracyber Config Retrieve",
                            affectedRecordId = null,
                            status = if (report.missingRoles != null) HttpStatus.FORBIDDEN.value() else HttpStatus.NOT_FOUND.value()
                    ),
                    errors = if (report.missingRoles != null) report.toStringMap() else report.invalidResourceId!!
            )

            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            result = if (report.missingRoles == null) {
                Result(errors = report.invalidResourceId!!, status = HttpStatus.NOT_FOUND)
            }
            // user did not have access to the resource
            else {
                Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }
}