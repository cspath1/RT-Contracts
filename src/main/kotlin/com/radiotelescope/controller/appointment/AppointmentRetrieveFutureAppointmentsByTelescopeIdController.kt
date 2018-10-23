package com.radiotelescope.controller.appointment

import org.springframework.web.bind.annotation.RestController
import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class AppointmentRetrieveFutureAppointmentsByTelescopeIdController (
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
):BaseRestController(logger) {

    //how do I get the pageRequest params?

    @PutMapping(value = ["/api/appointments/telescopes/{telescopeId}/retrieve"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@PathVariable("telescopeId") teleid: Long,
                @RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?
    ) {
        if (pageNumber == null || pageNumber < 0 || (pageSize == null) || pageSize <= 0) {
            val errors = pageErrors()

            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = Log.Action.LIST_FUTURE_APPOINTMENT_BY_TELESCOPE_ID,
                            affectedRecordId = null
                    ),

                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        } else {
            appointmentWrapper.retrieveFutureAppointmentsByTelescopeId(teleid, PageRequest.of(pageNumber, pageSize))
            {
                it.success?.let {

                    page ->
                    page.content.forEach {

                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        Log.AffectedTable.APPOINTMENT,
                                        action = Log.Action.LIST_FUTURE_APPOINTMENT_BY_TELESCOPE_ID,
                                        affectedRecordId = it.id
                                )
                        )
                    }
                    result = Result(data = it)
                }
                it.error?.let { errors ->
                    logger.createErrorLogs(
                            info = Logger.createInfo(affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = Log.Action.LIST_FUTURE_APPOINTMENT_BY_TELESCOPE_ID,
                                    affectedRecordId = null
                            ),
                            errors = errors.toStringMap())

                    result = Result(errors.toStringMap())

                }

            }?.let {

                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = Log.Action.LIST_FUTURE_APPOINTMENT_BY_TELESCOPE_ID,
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)

            }
        }

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