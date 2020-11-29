package com.radiotelescope.controller.sensorStatus

import com.radiotelescope.contracts.sensorStatus.UserSensorStatusWrapper
import com.radiotelescope.contracts.sensorStatus.GetMostRecent
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle Appointment retrieval
 *
 * @param sensorStatusWrapper the [UserSensorStatusWrapper]
 * @param logger the [Logger] service
 */
@RestController
class SensorStatusGetMostRecentController(
        private val sensorStatusWrapper: UserSensorStatusWrapper,
        logger: Logger
): BaseRestController(logger) {
    /**
     * Execute method that is in charge of executing the [UserSensorStatusWrapper.getMostRecent] method.
     * If this method returns an [AccessReport], this means they did not pass authentication and
     * we should respond with errors.
     *
     * Otherwise, this means the [GetMostRecent] command was executed, and the controller
     * will check whether or not this command was a success or not, responding
     * appropriately.
     */
    @GetMapping(value = ["/api/sensor-status/getMostRecent"])
    fun execute(): Result {
        sensorStatusWrapper.getMostRecent {
            // If the command was a success
            it.success?.let { data ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.SENSOR_STATUS,
                                action = "Sensor Status Most Recent Retrieval",
                                affectedRecordId = data.id,
                                status = HttpStatus.OK.value()
                        )
                )

                result = Result(data = data)
            }

            // Otherwise, it was an error
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Sensor Status Most Recent Retrieval",
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
            // record does not exist
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.SENSOR_STATUS,
                            action = "Sensor Status Most Recent Retrieval",
                            affectedRecordId = null,
                            status = if (it.missingRoles != null) HttpStatus.FORBIDDEN.value() else HttpStatus.NOT_FOUND.value()
                    ),
                    errors = if (it.missingRoles != null) it.toStringMap() else it.invalidResourceId!!
            )

            // Set the errors depending on if the user was not authenticated or the
            // record does not exist
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