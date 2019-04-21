package com.radiotelescope.controller.viewer

import com.radiotelescope.contracts.viewer.SharePrivateAppointment
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle Sharing Private Appointment
 *
 * @param viewerWrapper the [UserViewerWrapper]
 * @param awsSesSendService the [IAwsSesSendService]
 * @param appointmentRepo the [IAppointmentRepository]
 * @param logger the [Logger] service
 */
@RestController
class ViewerSharePrivateController(
        private val viewerWrapper: UserViewerWrapper,
        private val awsSesSendService: IAwsSesSendService,
        private val appointmentRepo: IAppointmentRepository,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of sharing a private appointment by executing
     * [UserViewerWrapper.sharePrivateAppointment] method
     *
     * If it was successful, it will notify the user that an appointment has been shared
     * with them
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/appointments/{appointmentId}/viewers"])
    fun execute(@PathVariable("appointmentId") appointmentId: Long,
                @RequestParam("email") email: String): Result {
        val request = SharePrivateAppointment.Request(
                email = email,
                appointmentId = appointmentId
        )
        viewerWrapper.sharePrivateAppointment(request) {
            // If the command was a success
            it.success?.let { id ->
                logger.createSuccessLog(
                        info = Logger.createInfo(Log.AffectedTable.VIEWER,
                                action = "Share Private Appointment",
                                affectedRecordId = id,
                                status = HttpStatus.OK.value()
                        )
                )
                result = Result(data = id)

                sendEmail(email, appointmentId)
            }
            // If the command was a failure
            it.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.VIEWER,
                                action = "Share Private Appointment",
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
                            action = "Share Private Appointment",
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


    private fun sendEmail(email: String, appointmentId: Long) {
        val sendForm = SendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                subject = "Share Private Appointment",
                htmlBody = "<p>Appointment #$appointmentId has been shared with you."
        )

        awsSesSendService.execute(sendForm)
    }
}