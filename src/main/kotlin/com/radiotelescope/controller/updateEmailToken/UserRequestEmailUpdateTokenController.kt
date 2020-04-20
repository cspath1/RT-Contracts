package com.radiotelescope.controller.updateEmailToken

import com.radiotelescope.contracts.updateEmailToken.UserUpdateEmailTokenWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.AppLink
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.controller.model.updateEmailToken.UpdateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.IAwsSesSendService
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
        private val awsSesSendService: IAwsSesSendService,
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
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
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
                                    affectedRecordId = null,
                                    status = HttpStatus.OK.value()
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
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
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
                                affectedRecordId = null,
                                status = HttpStatus.FORBIDDEN.value()
                        ),
                        errors = report.toStringMap()
                )

                result = Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }

    /**
     * Sends an email to the address the user wishes to be associated with their account,
     * allowing them to click a generated link which will associate the new email address
     * with their account.
     *
     * @param email the new email address
     * @param token the generated token
     */
    private fun sendEmail(email: String, token: String) {
        val resetPasswordLink = AppLink.generate(profile) + "/updateEmail?token=" + token

        val sendForm = SesSendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <info@astroyork.com>",
                subject = "Change Email Requested",
                htmlBody = "<p>You have requested to change your email</p>" +
                        "<p>This link will expire in one day. If it does, you must request another.</p>" +
                        "<p>Please <a href='$resetPasswordLink'> here to update your email</a></p>" +
                        "<p>If you did not request to update your email, please delete this email</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}