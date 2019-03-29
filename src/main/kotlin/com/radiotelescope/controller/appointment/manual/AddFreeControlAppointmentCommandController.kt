package com.radiotelescope.controller.appointment.manual

import com.radiotelescope.contracts.appointment.wrapper.UserManualAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.AddFreeControlAppointmentCommandForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AddFreeControlAppointmentCommandController(
        @Qualifier(value = "freeControlAppointmentWrapper")
        private val wrapper: UserManualAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    @PutMapping(value = ["/api/appointments/{appointmentId}/add-command"])
    fun execute(@RequestBody form: AddFreeControlAppointmentCommandForm,
                @PathVariable(value = "appointmentId") id: Long): Result {
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Add Free Control Appointment Command",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise, execute the wrapper command
        let {
            val request = form.toRequest()
            request.appointmentId = id
            wrapper.addCommand(
                    request = request
            ) { response ->
                // If the command was a success
                response.success?.let { data ->
                    // Create success logs
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Add Free Control Appointment Command",
                                    affectedRecordId = data,
                                    status = HttpStatus.OK.value()
                            )
                    )

                    result = Result(data = data)
                }
                // Otherwise, it was an error
                response.error?.let { errors ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Add Free Control Appointment Command",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            }?.let { report ->
                // If we get here, this means the user did not pass authorization
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Add Free Control Appointment Command",
                                affectedRecordId = null,
                                status = HttpStatus.FORBIDDEN.value()
                        ),
                        errors = report.toStringMap()
                )

                result = Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }
}