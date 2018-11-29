package com.radiotelescope.controller.updateEmailToken

import com.radiotelescope.contracts.updateEmailToken.UserUpdateEmailTokenWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.AppLink
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.model.updateEmailToken.UpdateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.AwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle User Request Reset Password Token
 *
 * @param updateEmailTokenWrapper the [UserUpdateEmailTokenWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserRequestEmailUpdateTokenController (
        private val updateEmailTokenWrapper: UserUpdateEmailTokenWrapper,
        private val profile: Profile,
        private val awsSesSendService: AwsSesSendService,
        logger: Logger
): BaseRestController(logger){

    /**
     * Execute method that is in charge of taking the incoming
     * email and pass it to the command to create reset password token
     *
     * @param userId the User's id
     * @param form the [UpdateForm] object
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/users/{userId}/updateEmail"])
    fun execute(@PathVariable ("userId") userId: Long,
                @RequestBody form: UpdateForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.UPDATE_EMAIL_TOKEN,
                            action = "User Update Email Request",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?: let {
            // Setting the request
            val request = form.toRequest()
            request.userId = userId

            // Otherwise call the factory command
            updateEmailTokenWrapper.requestUpdateEmail(
                    request = request
            ) { response ->
                // If the command was a success
                response.success?.let { data ->
                    result = Result(
                            data = data
                    )

                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.UPDATE_EMAIL_TOKEN,
                                    action = "User Update Email Request",
                                    affectedRecordId = null
                            )
                    )

                    sendEmail(
                            email = request.email,
                            token = data
                    )
                }
                // Otherwise, it was a failure
                response.error?.let { error ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.UPDATE_EMAIL_TOKEN,
                                    action = "User Update Email Request",
                                    affectedRecordId = null
                            ),
                            errors = error.toStringMap()
                    )
                    result = Result(
                            errors = error.toStringMap()
                    )
                }
            }?.let { report ->
                // If we get here, that means the user was not authenticated
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.UPDATE_EMAIL_TOKEN,
                                action = "User Update Email Request",
                                affectedRecordId = null
                        ),
                        errors = report.toStringMap()
                )

                result = Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }

    private fun sendEmail(email: String, token: String) {
        val resetPasswordLink = AppLink.generate(profile) + "/updateEmail?token=" + token

        val sendForm = SendForm(
                toAddresses = listOf(email),
                fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                subject = "Change Email Requested",
                htmlBody = "<p>You have requested to change your email</p>" +
                        "<p>This link will expire in one day. If it does, you must request another.</p>" +
                        "<p>Please <a href='$resetPasswordLink'> here to update your email</a></p>" +
                        "<p>If you did not request to update your email, please delete this email</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}