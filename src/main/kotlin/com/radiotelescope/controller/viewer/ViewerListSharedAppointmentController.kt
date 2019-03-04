package com.radiotelescope.controller.viewer

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.viewer.ErrorTag
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/**
 * REST Controller to handle grabbing a list of shared Appointment
 *
 * @param viewerWrapper the [UserViewerWrapper]
 * @param logger the [Logger] service
 */
class ViewerListSharedAppointmentController(
        private val viewerWrapper: UserViewerWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of checking if the page parameters are
     * not null. If they are, it will instead respond with errors
     *
     * Otherwise, it will execute the [UserViewerWrapper.listSharedAppointment] method.
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/user/{id}/sharedAppointment"])
    fun execute(@PathVariable("id") id: Long,
                @RequestParam("page") pageNumber: Int,
                @RequestParam("size") pageSize: Int): Result {
        if (pageNumber < 0 || pageSize <= 0) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Shared Appointment List Retrieval",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        } else {
            // Sort by most recent
            val sort = Sort(Sort.Direction.DESC, "end_time")
            viewerWrapper.listSharedAppointment(
                    userId = id,
                    pageable = PageRequest.of(pageNumber, pageSize, sort)
            ) {
                // If the command was a success
                it.success?.let {page ->
                    // Create success logs
                    page.forEach{ info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        affectedTable = Log.AffectedTable.APPOINTMENT,
                                        action = "Shared Appointment List Retrieval",
                                        affectedRecordId = info.id,
                                        status = HttpStatus.OK.value()
                                )
                        )
                    }
                    result = Result(data = page)
                }
                // If the command was a failure
                it.error?.let { errors ->
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Shared Appointment List Retrieval",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )
                    result = Result(errors = errors.toStringMap())
                }
            }?.let { report ->
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Shared Appointment List Retrieval",
                                affectedRecordId = null,
                                status = if (report.missingRoles != null) HttpStatus.FORBIDDEN.value() else HttpStatus.NOT_FOUND.value()

                ),
                        errors = report.toStringMap()
                )
                result = Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
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