package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.appointment.Cancel
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller used to cancel an appointment
 */
@RestController
class AppointmentCancelController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the appointmentId [PathVariable]
     * and executing the [UserAppointmentWrapper.cancel] method. If this method returns
     * an [AccessReport], this means the user did not pass authentication and it should
     * respond with errors.
     *
     * Otherwise, this means the [Cancel] command was executed, and the controller should
     * respond to the client based on if the command was a success or not
     */
    @PutMapping(value = ["/api/appointments/{appointmentId}/cancel"])
    fun execute(@PathVariable("appointmentId") appointmentId: Long): Result {
        appointmentWrapper.cancel(appointmentId) {
            // If the command was a success
            it.success?.let { id ->
                // Create success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Cancellation",
                                affectedRecordId = id
                        )
                )

                result = Result(data = id)
            }
            // Otherwise it was a failure
            it.error?.let { error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Cancellation",
                                affectedRecordId = null
                        ),
                        errors = error.toStringMap()
                )

                result = Result(errors = error.toStringMap())
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Appointment Cancellation",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            result = if (it.missingRoles != null) {
                Result(errors = it.toStringMap(), status = HttpStatus.NOT_FOUND)
            }
            // user did not have access to the resource
            else {
                Result(errors = it.invalidResourceId!!, status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }
}