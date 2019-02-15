package com.radiotelescope.controller.viewer

import com.radiotelescope.contracts.viewer.SharePrivateAppointment
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle Sharing Private Appointment
 *
 * @param viewerWrapper the [UserViewerWrapper]
 * @param awsSesSendService the [AwsSesSendService]
 * @param userRepo the [IUserRepository]
 * @param logger the [Logger] service
 */
@RestController
class ViewerSharePrivateController(
        private val viewerWrapper: UserViewerWrapper,
        private val awsSesSendService: IAwsSesSendService,
        private val userRepo: IUserRepository,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of sharing a private appointment
     *
     * If the email
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/user/{id}/appointment/{appointmentId}/sharePrivate"])
    fun execute(@PathVariable("id") id: Long,
                @PathVariable("appointmentId") appointmentId: Long,
                @RequestParam("userId") userId: Long): Result {
        val request = SharePrivateAppointment.Request(
                userId = userId,
                appointmentId = appointmentId
        )
        viewerWrapper.sharePrivateAppointment(request) {
            // If the command was a success
            it.success?.let { id ->
                logger.createSuccessLog(
                        info = Logger.createInfo(Log.AffectedTable.VIEWER,
                                action = "Share Private Appointment",
                                affectedRecordId = id
                        )
                )
                result = Result(data = id)

                val email = userRepo.findById(userId).get().email
                sendEmail(email, appointmentId)
            }
            // If the command was a failure
            it.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.VIEWER,
                                action = "Share Private Appointment",
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
                            affectedTable = Log.AffectedTable.VIEWER,
                            action = "Share Private Appointment",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )
            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }


    private fun sendEmail(email: String, appointmentId: Long) {
        val sendForm = SendForm(
                toAddresses = listOf(email),
                fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                subject = "Share Private Appointment",
                htmlBody = "<p>An appointment with the id #$appointmentId has been shared " +
                        "with you.</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}