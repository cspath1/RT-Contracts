package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import com.radiotelescope.contracts.appointment.MakePublic
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PutMapping

/**
 * REST Controller to handle Appointment Update
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentMakePublicController (
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
): BaseRestController(logger){
    /**
     * Execute method that is in charge of taking the appointmentId [PathVariable]
     * and executing the [UserAppointmentWrapper.makePublic] method. If this method
     * returns an [AccessReport], this means they did not pass authentication and
     * we should respond with errors.
     *
     * Otherwise, this means the [MakePublic] command was executed, and the controller
     * will check whether or not this command was a success or not, responding
     * appropriately.
     */
    @PutMapping(value = ["/api/appointments/{appointmentId}/makePublic"])
    fun execute(@PathVariable("appointmentId") id: Long) : Result {
        appointmentWrapper.makePublic(id) {
            // If the command was a success
            it.success?.let { id ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Make Appointment Public",
                                affectedRecordId = id,
                                status = HttpStatus.OK.value()
                        )
                )

                result = Result(data = id)
            }
            // Otherwise, it was an error
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                Log.AffectedTable.APPOINTMENT,
                                action = "Make Appointment Public",
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
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Make Appointment Public",
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