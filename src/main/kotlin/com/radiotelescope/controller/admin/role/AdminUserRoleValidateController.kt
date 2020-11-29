package com.radiotelescope.controller.admin.role

import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.contracts.role.Validate
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.role.ValidateForm
import com.radiotelescope.controller.model.ses.AppLink
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.controller.model.sns.SnsSubscribeForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.service.sns.IAwsSnsService
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST controller to handle validate a user's category of service
 *
 * @param roleWrapper the [UserUserRoleWrapper] interface
 * @param awsSesSendService the [IAwsSesSendService] interface
 * @param logger the [Logger] service
 */
@RestController
class AdminUserRoleValidateController(
        private val roleWrapper: UserUserRoleWrapper,
        private val profile: Profile,
        private val awsSesSendService: IAwsSesSendService,
        private val awsSnsService: IAwsSnsService,
        logger: Logger
) : BaseRestController(logger) {
    @Value("\${amazon.aws.sns.default-topic}")
    lateinit var defaultSendTopic: String

    /**
     * Execute method that is in charge of adapting a [ValidateForm]
     * to a [Validate.Request] command (if possible). Otherwise, it
     * will respond with errors.
     *
     * Once validated, it will call the [UserUserRoleWrapper.validate]
     * method, and respond accordingly
     *
     * @param validateForm the [ValidateForm] object
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PutMapping(value = ["/api/roles/validate"])
    fun execute(@RequestBody validateForm: ValidateForm): Result {
        validateForm.validateRequest()?.let { errors -> 
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER_ROLE,
                            action = "User Role Validation",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        } ?: let {
            // Otherwise, call the wrapper command
            roleWrapper.validate(
                    request = validateForm.toRequest()
            ) { response ->
                // If the request was a success
                response.success?.let { theResponse ->
                    // Create success log
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.USER_ROLE,
                                    action = "User Role Validation",
                                    affectedRecordId = theResponse.id,
                                    status = HttpStatus.OK.value()
                            )
                    )

                    subscribeEndpoint(
                            endpoint = theResponse.email,
                            type = "email"
                    )

                    // If the user has input a phone number, subscribe it to the default topic
                    if (theResponse.phoneNumber != null) {
                        subscribeEndpoint(
                                endpoint = theResponse.phoneNumber,
                                type = "sms"
                        )
                    }

                    sendEmail(
                            email = theResponse.email,
                            token = theResponse.token
                    )

                    result = Result(data = theResponse.id)
                }
                // Otherwise it was a failure
                response.error?.let { errors ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.USER_ROLE,
                                    action = "User Role Validation",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )
                    
                    result = Result(errors = errors.toStringMap())
                }
            }?.let { report ->
                // If we get here, user authentication failed
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER_ROLE,
                                action = "User Role Validation",
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
     * Subscribe a user to the default announcement topic.
     *
     * @param endpoint the endpoint to subscribe
     * @param type the type of the endpoint: must be "sms" or "email"
     */
    private fun subscribeEndpoint(endpoint: String, type: String) {
        val subscribeForm = SnsSubscribeForm(
                topic = defaultSendTopic,
                protocol = type,
                endpoint = endpoint
        )

        awsSnsService.subscribe(subscribeForm)
    }

    private fun sendEmail(email: String, token: String) {
        val activateAccountLink = AppLink.generate(profile) + "/activateAccount?token=" + token

        val sendForm = SesSendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <info@astroyork.com>",
                subject = "Account Approved",
                htmlBody = "<p>Your account has been approved!</p>" +
                        "<p>Please <a href='$activateAccountLink'>click here </a> to activate your account so you can start</p> " +
                        "<p>using the application to conduct observations! Please note that this link will expire in 2 days</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}