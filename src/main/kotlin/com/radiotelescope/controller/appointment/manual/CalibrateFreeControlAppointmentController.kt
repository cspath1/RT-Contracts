package com.radiotelescope.controller.appointment.manual

import com.radiotelescope.contracts.appointment.wrapper.UserManualAppointmentWrapper
import com.radiotelescope.contracts.appointment.manual.CalibrateFreeControlAppointment
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle calibrating a Free Control Appointment
 *
 * @param wrapper the [UserManualAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class CalibrateFreeControlAppointmentController(
        @Qualifier(value = "freeControlAppointmentWrapper")
        private val wrapper: UserManualAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that will take the appointment id path variable and call the
     * [UserManualAppointmentWrapper.calibrateAppointment] method.
     *
     * If this method returns an [AccessReport], this means the user did not pass
     * authorization, and the controller will respond with errors.
     *
     * Otherwise, the [CalibrateFreeControlAppointment] command was executed, and the
     * controller will respond based on if this command was a success or not.
     */
    @PutMapping(value = ["/api/appointments/{appointmentId}/calibrate"])
    fun execute(@PathVariable(value = "appointmentId") id: Long): Result {
        wrapper.calibrateAppointment(id) { response ->
            // If the command was a success
            response.success?.let { data ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Calibrate Free Control Appointment",
                                affectedRecordId = data,
                                status = HttpStatus.OK.value()
                        )
                )

                result = Result(data = data)
            }
            // Otherwise it was a failure
            response.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Calibrate Free Control Appointment",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }?.let {report ->
            // If we get here, this means the user did not pass authorization
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Calibrate Free Control Appointment",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = report.toStringMap()
            )

            result = Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}