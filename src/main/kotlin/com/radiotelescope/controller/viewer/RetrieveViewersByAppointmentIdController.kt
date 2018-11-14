package com.radiotelescope.controller.viewer

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

/**
 * Controller to interface with front-end with REST API endpoints for getting viewers of an Appointment, by Appointment Id
 * @param userViewerWrapper [UserViewerWrapper] on which to call getViewersByAppointmentId
 * @return BaseRestController
 *
 */

class RetrieveViewersByAppointmentIdController(
        private val userViewerWrapper: UserViewerWrapper,
        logger: Logger
): BaseRestController(logger)
{

    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/viewers/retrieve/byAppointment/{appointmentId}"])

    fun execute(@PathVariable("appointmentId") appt_id:Long,
                @RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?
                ): Result
    {

        if (pageNumber == null || pageSize == null || pageNumber < 0 || pageSize <= 0)
        {
            val errors = pageErrors()

            logger.createErrorLogs(
                    info = Logger.createInfo(
                            Log.AffectedTable.VIEWER,
                            action = "Retrieve Viewers By Appointment Id",
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )
            return Result( data = errors)
        }

        let {
            userViewerWrapper.getViewersByAppointmentId(appt_id, PageRequest.of(pageNumber, pageSize)){
                //fail case
                if (it.success == null)
                {
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.VIEWER,
                                    action = "Request for Retrieve Viewers By Appointment Id",
                                    affectedRecordId = null
                            ),
                            errors = it.error!!.toStringMap()
                    )
                    result = Result(data = it.error)
                }
                //success case
                else
                {
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.VIEWER,
                                    action = "Request for Retrieve Viewers By Appointment Id",
                                    affectedRecordId = null
                            )
                    )
                    result = Result(data = it.success)
                }
            }
        }
        return result
    }

    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}

