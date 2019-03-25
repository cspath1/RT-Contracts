package com.radiotelescope.controller.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.appointment.wrapper.UserAutoAppointmentWrapper
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


/**
 * Rest Controller to handle listing future appointments for a user
 *
 * @param autoAppointmentWrapper the [UserAutoAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentListFutureAppointmentsByUserController(
        @Qualifier(value = "coordinateAppointmentWrapper")
        private val autoAppointmentWrapper: UserAutoAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of returning a user's future appointments.
     *
     * If the [pageNumber] or [pageSize] request parameters are null or invalid,
     * respond with errors. Otherwise, call the [UserAutoAppointmentWrapper.userFutureList]
     * method. If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/users/{userId}/appointments/futureList"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestParam("page") pageNumber: Int,
                @RequestParam("size") pageSize: Int): Result {
        // If any of the request params are null, respond with errors
        if(pageNumber < 0 || pageSize <= 0) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "User Future Appointment List Retrieval",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val sort = Sort(Sort.Direction.ASC, "start_time")
            autoAppointmentWrapper.userFutureList(userId, PageRequest.of(pageNumber, pageSize, sort)) {
                //If the command was a success
                it.success?.let{ page ->
                    // Create success logs
                    page.content.forEach { info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(Log.AffectedTable.APPOINTMENT,
                                        action = "User Future Appointment List Retrieval",
                                        affectedRecordId = info.id,
                                        status = HttpStatus.OK.value()
                                )
                        )
                    }
                    result = Result(data = page)
                }
                // If the command was a failure
                it.error?.let{ errors ->
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "User Future Appointment List Retrieval",
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
                                action = "User Future Appointment List Retrieval",
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

    /**
     * Private method to return a [HashMultimap] of errors in the event
     * that the page size and page number are invalid
     */
    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}