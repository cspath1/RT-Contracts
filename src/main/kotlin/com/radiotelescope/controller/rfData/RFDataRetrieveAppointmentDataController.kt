package com.radiotelescope.controller.rfData

import com.radiotelescope.contracts.rfdata.UserRFDataWrapper
import com.radiotelescope.contracts.rfdata.RetrieveAppointmentData
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle retrieving a completed appointments data
 *
 * @param rfDataWrapper the [UserRFDataWrapper]
 * @param logger the [Logger] service
 */
@RestController
class RFDataRetrieveAppointmentDataController(
        private val rfDataWrapper: UserRFDataWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the appointment id [PathVariable]
     * and executing the [UserRFDataWrapper.retrieveAppointmentData] method. If this
     * returns an [AccessReport], this means the user accessing this endpoint did not
     * pass authentication and it should respond with the errors.
     *
     * Otherwise, this means the [RetrieveAppointmentData] command was executed, and the
     * controller will check whether or not this command was a success or not, and
     * respond accordingly
     */
    @GetMapping(value = ["/api/appointments/{appointmentId}/rf-data"])
    fun execute(@PathVariable("appointmentId") appointmentId: Long): Result {
        rfDataWrapper.retrieveAppointmentData(appointmentId) {
            // If the command was a success
            it.success?.let { list ->
                // Create success logs
                list.forEach { info ->
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.RF_DATA,
                                    action = "Appointment RF Data Retrieval",
                                    affectedRecordId = info.id
                            )
                    )
                }

                result = Result(data = list)
            }
            // Otherwise, it was an error
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.RF_DATA,
                                action = "Appointment RF Data Retrieval",
                                affectedRecordId = null
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
                            affectedTable = Log.AffectedTable.RF_DATA,
                            action = "Appointment RF Data Retrieval",
                            affectedRecordId = null
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