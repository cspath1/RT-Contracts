package com.radiotelescope.controller.thresholds

import com.radiotelescope.contracts.thresholds.UserThresholdsWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle retrieving a list of the current Sensor Overrides status
 *
 * @param thresholdsWrapper the [UserThresholdsWrapper]
 * @param logger the [Logger] service
 */
@RestController
class ThresholdsRetrieveListController (
        private val thresholdsWrapper: UserThresholdsWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute the [UserThresholdsWrapper.update] method.
     * Based on the result of this call, the method will either respond with
     * the data or the errors from the method call.
     */
    @GetMapping(value = ["/api/thresholds/retrieve"])
    fun execute(): Result {
        thresholdsWrapper.retrieveList() {
            // If the command was a success
            it.success?.let { info ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.THRESHOLDS,
                                action = "Thresholds Retrieval List",
                                affectedRecordId = null,
                                status = HttpStatus.OK.value()
                        )
                )

                result = Result(data = info)
            }
            // Otherwise, it was an error
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.THRESHOLDS,
                                action = "Thresholds Retrieval List",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }?.let {
            // If we get here, that means the User did not pass authentication

            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.THRESHOLDS,
                            action = "Thresholds Retrieval List",
                            affectedRecordId = null,
                            status = if (it.missingRoles != null) HttpStatus.FORBIDDEN.value() else HttpStatus.NOT_FOUND.value()
                    ),
                    errors = if (it.missingRoles != null) it.toStringMap() else it.invalidResourceId!!
            )

            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            result = if (it.missingRoles == null) {
                Result(errors = it.invalidResourceId!!, status = HttpStatus.NOT_FOUND)
            }
            // user did not have access to the resource
            else {
                Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }
}