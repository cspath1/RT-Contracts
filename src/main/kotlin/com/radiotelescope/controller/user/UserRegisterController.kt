package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.contracts.user.Register
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.controller.model.user.RegisterForm
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.AppLink
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.AwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle User Registration
 *
 * @param userWrapper the [UserUserWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserRegisterController(
        private val userWrapper: UserUserWrapper,
        private val profile: Profile,
        private val awsSesSendService: AwsSesSendService,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the incoming
     * [RegisterForm] object and seeing if it can be adapted to a
     * [Register.Request] object.
     *
     * If so, it will be adapted and the execute method for the
     * respective command will be called.
     *
     * @param form the [RegisterForm]
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/users"])
    fun execute(@RequestBody form: RegisterForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Registration",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise execute the wrapper command
        let { _ ->
            val simpleResult = userWrapper.register(
                    request = form.toRequest()
            ).execute()
            // If the command was a success
            simpleResult.success?.let {
                result = Result(
                        data = it
                )
                // Create a success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Registration",
                                affectedRecordId = it.id
                        )
                )

                sendEmail(
                        email = simpleResult.success.email,
                        token = simpleResult.success.token
                )
            }
            // Otherwise, it was a failure
            simpleResult.error?.let {
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Registration",
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(
                        errors = it.toStringMap()
                )
            }
        }

        return result
    }

    private fun sendEmail(email: String, token: String) {
        val activateAccountLink = AppLink.generate(profile) + "?token=" + token

        val sendForm = SendForm(
                toAddresses = listOf(email),
                fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                subject = "YCP Radio Telescope Sign Up",
                htmlBody = "<p>Please click <a href='$activateAccountLink'> here to activate your account</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}