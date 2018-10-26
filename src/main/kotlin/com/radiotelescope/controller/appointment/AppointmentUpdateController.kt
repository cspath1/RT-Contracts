package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.UpdateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle Appointment Update
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentUpdateController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
): BaseRestController(logger){
    /**
     * Execute method that is in charge of adapting the [UpdateForm]
     * into a [Update.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserAppointmentWrapper.update] method.
     * If this method returns an [AccessReport]
     */
    @PutMapping(value = ["/api/appointments/{appointmentId}"])
    fun execute(@PathVariable("appointmentId") appointmentId: Long,
                @RequestBody form: UpdateForm
    ): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Appointment Update",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )
            result = Result(errors = it.toStringMap())
        }?: let{ _ ->
            // Otherwise call the factory command
            val request = form.toRequest()

            // Setting the appointmentId for the request
            request.id = appointmentId

            appointmentWrapper.update(
                    request = request
            ) { it ->
                it.success?.let{
                    result = Result(
                            data = it
                    )

                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Appointment Update",
                                    affectedRecordId = it
                            )
                    )
                }
                it.error?.let{
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Appointment Retrieval",
                                    affectedRecordId = null
                            ),
                            errors = it.toStringMap()
                    )
                    result = Result(
                            errors = it.toStringMap()
                    )
                }
            }?.let {
                // If we get here, that means the User did not pass authentication

                // Set the errors depending on if the user was not authenticated or the
                // record did not exists
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Update",
                                affectedRecordId = null
                        ),
                        errors = if (it.missingRoles != null) it.toStringMap() else it.invalidResourceId!!
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
        }

        return result
    }
}