package com.radiotelescope.controller.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.ListBetweenDatesForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Rest Controller to handle listing appointments between two dates
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentListBetweenDates (
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger){
    /**
     * Execute method that is in charge of returning a list of appointments
     * between the two given time.
     *
     * If the fields in the [ListBetweenDatesForm] are null or invalid,
     * respond with errors. Otherwise, call the [UserAppointmentWrapper.appointmentListBetweenDates]
     * method. If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/appointments/telescopes/{telescopeId}/listBetweenDates"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@RequestBody form: ListBetweenDatesForm,
                @PathVariable("telescopeId") telescopeId: Long
    ): Result {
        // If any of the request params are null, respond with errors
        if(form.startTime == null || form.endTime == null) {
            val errors = timeErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Appointment List Between Times",
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            appointmentWrapper.listBetweenDates(
                    startTime = form.startTime,
                    endTime = form.endTime,
                    telescopeId = telescopeId) { it ->
                //If the command was a success
                it.success?.let{ list ->
                    // Create success logs
                    list.forEach{
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        Log.AffectedTable.APPOINTMENT,
                                        action = "Appointment List Between Times",
                                        affectedRecordId = it.id
                                )
                        )
                    }
                    result = Result(data = it)
                }
                // If the command was a failure
                it.error?.let{ errors ->
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Appointment List Between Times",
                                    affectedRecordId = null
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
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }

    /**
     * Private method to return a [HashMultimap] of errors in the event
     * that the start time and end time are invalid
     */
    private fun timeErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.TIME, "Invalid start or end time parameters")
        return errors
    }
}