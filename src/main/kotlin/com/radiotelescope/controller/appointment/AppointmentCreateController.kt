package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.appointment.CoordinateCreate
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.CoordinateCreateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle Appointment Creation
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentCreateController(
        @Qualifier(value = "coordinateCreateAppointmentWrapper")
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [CoordinateCreateForm]
     * into a [CoordinateCreate.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserAppointmentWrapper.create] method.
     * If this method returns an [AccessReport], this means the user did not pass
     * authentication and the method will respond with errors.
     *
     * Otherwise, the [CoordinateCreate] command was executed, and the controller will check
     * whether this command was a success or not, responding appropriately.
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/appointments/schedule"])
    fun execute(@RequestBody form: CoordinateCreateForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Appointment Creation",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise, execute the wrapper command
        let {
            appointmentWrapper.create(
                    request = form.toRequest()
            ) { response ->
                // If the command called was a success
                response.success?.let { data ->
                    // Create success logs
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Appointment Creation",
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
                                    action ="Appointment Creation",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            }?.let { report ->
                // If we get here, this means the User did not pass authentication
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Creation",
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