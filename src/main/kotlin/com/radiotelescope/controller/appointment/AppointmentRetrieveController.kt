package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.appointment.Retrieve
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle Appointment retrieval
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentRetrieveController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the appointmentId [PathVariable]
     * and executing the [UserAppointmentWrapper.retrieve] method. If this method
     * returns an [AccessReport], this means they did not pass authentication and
     * we should respond with errors.
     *
     * Otherwise, this means the [Retrieve] command was executed, and the controller
     * will check whether or not this command was a success or not, responding
     * appropriately.
     */
    @GetMapping(value = ["users/appointment/{appointmentId}/retrieve"])
    fun execute(@PathVariable("appointmentId") id: Long): Result {
        appointmentWrapper.retrieve(id) { it ->
            // If the command was a success
            it.success?.let {
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = Log.Action.RETRIEVE,
                                affectedRecordId = it.id
                        )
                )

                result = Result(data = it)
            }
            // Otherwise, it was an error
            it.error?.let {
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                Log.AffectedTable.APPOINTMENT,
                                action = Log.Action.RETRIEVE,
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap())
            }
        }?.let {
            // If we get here, that means the User did not pass authentication
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = Log.Action.RETRIEVE,
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}