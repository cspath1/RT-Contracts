package com.radiotelescope.controller.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * Rest Controller used to retrieve a list of the users completed appointments
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentCompletedUserListController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of, given that valid page parameters
     * were supplied, calling the [UserAppointmentWrapper.userCompleteList]
     * method and responding back to the client-side based on if the user
     * was authenticated, the command was executed and was a success, or the
     * command was executed and was a failure
     */
    @GetMapping(value = ["/api/users/{userId}/appointments/completedList"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?): Result {
        // If any of the request params are null, respond with errors
        if ((pageNumber == null || pageNumber < 0) || (pageSize == null || pageSize <= 0)) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "User Completed Appointment List",
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val sort = Sort(Sort.Direction.DESC, "end_time")
            appointmentWrapper.userCompleteList(
                    userId = userId,
                    pageable = PageRequest.of(pageNumber, pageSize, sort)
            ) {
                // If the command was a success
                it.success?.let { page ->
                    // Create success logs
                    page.content.forEach { it ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        affectedTable = Log.AffectedTable.APPOINTMENT,
                                        action = "User Completed Appointment List",
                                        affectedRecordId = it.id
                                )
                        )
                    }

                    result = Result(data = page)
                }
                // Otherwise, it was a failure
                it.error?.let { error ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "User Completed Appointment List",
                                    affectedRecordId = null
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
                                action = "User Completed Appointment List",
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