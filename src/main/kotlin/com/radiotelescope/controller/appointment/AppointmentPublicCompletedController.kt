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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle retrieving a list of public completed appointments
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentPublicCompletedController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of, given the page parameters were passed in
     * correctly, calling the [UserAppointmentWrapper.publicCompletedAppointments]
     * method and responding back to the client based on if the user was authenticated
     * or not.
     */
    @GetMapping(value = ["/api/appointments/publicCompleted"])
    fun execute(@RequestParam("page") pageNumber: Int,
                @RequestParam("size") pageSize: Int): Result {
        if (pageNumber < 0 || pageSize <= 0) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Completed Public Appointment List Retrieval",
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        } else {
            // Sort by most recent
            val sort = Sort(Sort.Direction.DESC, "end_time")
            appointmentWrapper.publicCompletedAppointments(
                    pageable = PageRequest.of(pageNumber, pageSize, sort)
            ) {
                // If the command was a success
                // NOTE: As of now, there is no scenario
                // where the command object's execute method
                // will return errors
                it.success?.let { page ->
                    // Create success logs
                    page.forEach { info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        affectedTable = Log.AffectedTable.APPOINTMENT,
                                        action = "Completed Public Appointment List Retrieval",
                                        affectedRecordId = info.id
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
                                action = "Completed Public Appointment List Retrieval",
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
     * Private method to return a [HashMultimap] of errors in the
     * event that invalid page parameters were provided
     */
    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}