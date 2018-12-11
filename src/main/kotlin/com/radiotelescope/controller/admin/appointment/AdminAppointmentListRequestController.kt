package com.radiotelescope.controller.admin.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * Rest Controller to handle listing appointment requests
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminAppointmentListRequestController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger){
    /**
     * Execute method that is in charge of returning a user's future appointments.
     *
     * If the [pageNumber] or [pageSize] request parameters are null or invalid,
     * respond with errors. Otherwise, call the [UserAppointmentWrapper.listRequest]
     * method. If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/appointments/listRequested"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?): Result {
        // If any of the request params are null, respond with errors
        if((pageNumber == null || pageNumber < 0) || (pageSize == null || pageSize <= 0)) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "User Request List Of Appointment Request",
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val sort = Sort(Sort.Direction.ASC, "start_time")
            appointmentWrapper.listRequest(PageRequest.of(pageNumber, pageSize, sort)) { it ->
                // NOTE: This command currently only has a success scenario
                // (given the user is authenticated)
                //If the command was a success
                it.success?.let{ page ->
                    // Create success logs
                    page.content.forEach{
                        logger.createSuccessLog(
                                info = Logger.createInfo(Log.AffectedTable.APPOINTMENT,
                                        action = "Requested Appointment List",
                                        affectedRecordId = it.id
                                )
                        )
                    }
                    result = Result(data = page)
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Requested Appointment List",
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
     * that the page size and page number are invalid
     */
    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}