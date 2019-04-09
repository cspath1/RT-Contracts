package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.Invite
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.AppLink
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AccessReport
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle retrieving User information
 *
 * @param userWrapper the [UserUserWrapper]
 * @param profile a [Profile] used to generate the link to the site
 * @param userRepo the [IUserRepository]
 * @param awsSesSendService the [IAwsSesSendService]
 * @param logger the [Logger] service
 */
@RestController
class UserInviteController(
        private val userWrapper: UserUserWrapper,
        private val profile: Profile,
        private val userRepo: IUserRepository,
        private val awsSesSendService: IAwsSesSendService,
        logger: Logger
): BaseRestController(logger){
    /**
     * Execute method that is in charge of taking the id [PathVariable]
     * and making sure it is not null. If it is, respond with an error.
     *
     * Otherwise, execute the [UserUserWrapper.invite] method. If this
     * method returns an [AccessReport] respond with the errors. If not,
     * this means the [Invite] command was executed, check if the
     * method was a success or not
     *
     * @param id the User's id
     * @param email the email to send an invite to
     */
    @PostMapping(value = ["/api/users/{id}/invite"])
    fun execute(@PathVariable("id") id: Long,
                @RequestParam email: String): Result{
        // If the supplied path variable is not null, call the invite
        userWrapper.invite(email) {
            // If the command called after successful validation is a success
            it.success?.let { value ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "Invite User",
                                affectedRecordId = null,
                                status = HttpStatus.OK.value()
                        )
                )
                result = Result(data = value)
            }
            // Otherwise, it was an error
            it.error?.let{ error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "Invite User",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = error.toStringMap()
                )

                result = Result(errors = error.toStringMap())

                sendEmail(email, userRepo.findById(id).get())
            }
        }?.let{
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "Invite User",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    private fun sendEmail(email: String, user: User){
        val registerLink = AppLink.generate(profile) + "/users/register"
        val name = user.firstName + " " + user.lastName

        val sendForm = SendForm(
                toAddresses = listOf(email),
                fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                subject = "Invitation to Join the Radio Telescope",
                htmlBody = "<p>$name has invited you to join the York County Astronomical Society's " +
                        "Radio Telescope web application!</p>" +
                        "<p>Please click <a href='$registerLink'> here to register for an account</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}