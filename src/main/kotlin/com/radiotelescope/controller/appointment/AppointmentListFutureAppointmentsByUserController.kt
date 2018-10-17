package com.radiotelescope.controller.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable


/**
 * Rest Controller to handle listing future appointments for a user
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentListFutureAppointmentsByUserController (
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of returning a user's future appointments.
     *
     * If the [pageNumber] or [pageSize] request parameters are null or invalid,
     * respond with errors. Otherwise, call the [UserAppointmentWrapper.getFutureAppointmentsForUser]
     * method. If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/users/{userId}/appointments/futureList"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?) {
        //If any of the request params are null, respond with errors
        if((pageNumber == null || pageNumber < 0) || (pageSize == null || pageSize <= 0)){
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = Log.Action.LIST_FUTURE_APPOINTMENT_BY_USER,
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            appointmentWrapper.getFutureAppointmentsForUser(userId, PageRequest.of(pageNumber, pageSize)) { it ->
                //If the command was a success
                it.success?.let{ page ->
                    // Create success logs
                    page.content.forEach{
                        logger.createSuccessLog(
                                info = Logger.createInfo(Log.AffectedTable.APPOINTMENT,
                                        action = Log.Action.LIST_FUTURE_APPOINTMENT_BY_USER,
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
                                    action = Log.Action.LIST_FUTURE_APPOINTMENT_BY_USER,
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
                                action = Log.Action.LIST_FUTURE_APPOINTMENT_BY_USER,
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }
    }

    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}