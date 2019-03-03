package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle retrieving available time
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentAvailableTimeController (
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of returning remaining available
     * time.
     *
     * Call the [UserAppointmentWrapper.userAvailableTime]
     * method. If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/users/{userId}/appointments/remainingSchedulingTime"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@PathVariable("userId") userId: Long): Result {
        appointmentWrapper.userAvailableTime(userId) {
            // If the command was a success
            it.success?.let { time ->
                // Create success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Remaining Scheduling Time",
                                affectedRecordId = null,
                                status = HttpStatus.OK.value()
                        )
                )

                result = Result(data = time)
            }
            // Otherwise it was a failure
            it.error?.let { error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Remaining Scheduling Time",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
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
                            action = "Appointment Remaining Scheduling Time",
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