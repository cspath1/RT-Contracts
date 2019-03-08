package com.radiotelescope.controller.viewer

import com.radiotelescope.contracts.viewer.UnSharePrivateAppointment
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class ViewerUnSharePrivateController(
        private val viewerWrapper: UserViewerWrapper,
        logger: Logger
) : BaseRestController(logger) {

    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/appointments/{appointmentId}/viewers"])
    fun execute(@PathVariable("appointmentId") appointmentId: Long,
                @RequestParam("userId") userId: Long): Result {
        val request = UnSharePrivateAppointment.Request(
                userId = userId,
                appointmentId = appointmentId
        )
        viewerWrapper.unSharePrivateAppointment(request) {
            // If the command was a success
            it.success?.let {id ->
                logger.createSuccessLog(
                        info = Logger.createInfo(Log.AffectedTable.VIEWER,
                                action = "UnShare Private Appointment",
                                affectedRecordId = id,
                                status = HttpStatus.OK.value()
                        )
                )
                result = Result(data = id)
            }
            // If the command was a failure
            it.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.VIEWER,
                                action = "UnShare Private Appointment",
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
            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.VIEWER,
                            action = "UnShare Private Appointment",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = if (it.missingRoles != null) it.toStringMap() else it.invalidResourceId!!
            )
            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            result = if (it.missingRoles == null) {
                Result(errors = it.invalidResourceId!!, status = HttpStatus.NOT_FOUND)
            }
            // user did not have access to the resource
            else {
                Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }
        return result
    }
}