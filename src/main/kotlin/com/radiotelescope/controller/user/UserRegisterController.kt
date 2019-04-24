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
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle User Registration
 *
 * @param userWrapper the [UserUserWrapper]
 * @param awsSesSendService the [IAwsSesSendService] interface
 * @param userRepo the [IUserRepository] interface
 * @param logger the [Logger] service
 */
@RestController
class UserRegisterController(
        private val userWrapper: UserUserWrapper,
        private val awsSesSendService: IAwsSesSendService,
        private val userRepo: IUserRepository,
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
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise execute the wrapper command
        let {
            val simpleResult = userWrapper.register(
                    request = form.toRequest()
            ).execute()
            // If the command was a success
            simpleResult.success?.let { data ->
                result = Result(
                        data = data
                )
                // Create a success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Registration",
                                affectedRecordId = data.id,
                                status = HttpStatus.OK.value()
                        )
                )

                sendEmailToOwner(
                        email = simpleResult.success.email
                )
                sendEmailToAdmins(
                        emails = userRepo.findAllAdminEmail()
                )
            }
            // Otherwise, it was a failure
            simpleResult.error?.let { error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Registration",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = error.toStringMap()
                )

                result = Result(
                        errors = error.toStringMap()
                )
            }
        }

        return result
    }

    private fun sendEmailToOwner(email: String) {
        val sendForm = SendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                subject = "Account Created",
                htmlBody = "<p>Thank you for creating an account for the York County Astronomical Society's " +
                        "Radio Telescope web application! " +
                        "Once your account is approved, an activation token will be sent to you through this email " +
                        "for you to activate your account.</p>"
        )

        awsSesSendService.execute(sendForm)
    }

    private fun sendEmailToAdmins(emails: List<String>) {
        val sendForm = SendForm(
                toAddresses = emails,
                fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                subject = "Account and Role Approval",
                htmlBody = "<p>A new account has been created that requires an Admin approval/validation.</p>"
        )
        awsSesSendService.execute(sendForm)
    }
}