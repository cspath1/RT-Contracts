package com.radiotelescope.controller.resetPasswordToken

import com.radiotelescope.contracts.resetPasswordToken.UserResetPasswordTokenWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.AppLink
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle User Request Reset Password Token
 *
 * @param resetPasswordTokenWrapper the [UserResetPasswordTokenWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserResetPasswordRequestController (
        private val resetPasswordTokenWrapper: UserResetPasswordTokenWrapper,
        private val profile: Profile,
        private val awsSesSendService: IAwsSesSendService,
        logger: Logger
): BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the incoming
     * email and pass it to the command to create reset password token
     *
     * @param email the user email
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/requestPasswordReset"])
    fun execute(@RequestBody email: String): Result {
        val simpleResult = resetPasswordTokenWrapper.requestPasswordReset(
                email = email
        ).execute()
        // If the command was a success
        simpleResult.success?.let { data ->
            // Create a success log
            logger.createSuccessLog(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.RESET_PASSWORD_TOKEN,
                            action = "Request Password Reset",
                            affectedRecordId = null,
                            status = HttpStatus.OK.value()
                    )
            )

            result = Result(data = data)

            sendEmail(
                    email = email,
                    token = data
            )
        }
        // If the command was a failure
        simpleResult.error?.let { error ->
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.RESET_PASSWORD_TOKEN,
                            action = "Request Password Reset",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = error.toStringMap()
            )

            result = Result(errors = error.toStringMap())
        }

        return result
    }

    /**
     * Send an email to the user that requested to reset their password, allowing them to
     * click a generated link and access the form that allows them to do this.
     *
     * @param email the email address entered by the user
     * @param token the generated token
     */
    private fun sendEmail(email: String, token: String) {
        val resetPasswordLink = AppLink.generate(profile) + "/resetPassword?token=" + token

        val sendForm = SesSendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <info@astroyork.com>",
                subject = "Password Reset Requested",
                htmlBody = "<p>You have requested to reset you password</p>" +
                        "<p>This link will expire in one day. If it does, you must request another.</p>" +
                        "<p>Please <a href='$resetPasswordLink'> click here to reset your password</a></p>" +
                        "<p>If you did not request to reset your password, please delete this email</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}
