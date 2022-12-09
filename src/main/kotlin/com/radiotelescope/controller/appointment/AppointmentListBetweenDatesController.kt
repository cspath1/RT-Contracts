package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.wrapper.UserAutoAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.ListBetweenDatesForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Rest Controller to handle listing appointments between two dates
 *
 * @param autoAppointmentWrapper the [UserAutoAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentListBetweenDatesController (
        @Qualifier(value = "coordinateAppointmentWrapper")
        private val autoAppointmentWrapper: UserAutoAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger){
    /**
     * Execute method that is in charge of returning a list of appointments
     * between the two given times.
     *
     * If the fields in the [ListBetweenDatesForm] are null or invalid,
     * respond with errors. Otherwise, call the [UserAutoAppointmentWrapper.listBetweenDates]
     * method. If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/appointments/telescopes/{telescopeId}/listBetweenDates"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startTime: Date,
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endTime: Date,
                @PathVariable("telescopeId") telescopeId: Long
    ): Result {
        val form = ListBetweenDatesForm(
                startTime = startTime,
                endTime = endTime
        )
        // If any of the request params are null, respond with errors
        val errors = form.validateRequest()
        if(errors != null) {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Appointment List Between Times",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val request = form.toRequest()
            request.telescopeId = telescopeId
            autoAppointmentWrapper.listBetweenDates(request) {
                //If the command was a success
                it.success?.let { list ->
                    // Create success logs
                    list.forEach { info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        Log.AffectedTable.APPOINTMENT,
                                        action = "Appointment List Between Times",
                                        affectedRecordId = info.id,
                                        status = HttpStatus.OK.value()
                                )
                        )
                    }
                    result = Result(data = list)
                }
                // If the command was a failure
                it.error?.let { errors ->
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Appointment List Between Times",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment List Between Times",
                                affectedRecordId = null,
                                status = HttpStatus.FORBIDDEN.value()
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }
}