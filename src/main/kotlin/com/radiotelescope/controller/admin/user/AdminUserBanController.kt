package com.radiotelescope.controller.admin.user

import com.radiotelescope.contracts.user.Ban
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle an admin banning a user
 *
 * @param userWrapper the [UserUserWrapper]
 * @param userRepo the [IUserRepository]
 * @param awsSesSendService the [IAwsSesSendService]
 * @param logger the [Logger] service
 */
@RestController
class AdminUserBanController(
        private val userWrapper: UserUserWrapper,
        private val userRepo: IUserRepository,
        private val awsSesSendService: IAwsSesSendService,
        logger: Logger
): BaseRestController(logger) {
    /**
     * Execute method that is in charge of calling the [UserUserWrapper.ban]
     * method five then [userId] [PathVariable].
     *
     * If this method returns an [AccessReport], this means the user accessing the
     * endpoint did not pass authentication.
     *
     * Otherwise the [Ban] command was executed, and the controller should
     * respond based on whether or not the command was a success or not
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PutMapping(value = ["/api/users/{userId}/ban"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestParam(
                        value = "message",
                        required = false,
                        defaultValue = "") message: String?
    ): Result {
        userWrapper.ban(id = userId) {
            // If the command was a success
            it.success?.let { id ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Ban",
                                affectedRecordId = id,
                                status = HttpStatus.OK.value()
                        )
                )
                sendEmail(userRepo.findById(id).get().email, message)

                result = Result(data = id)
            }
            // Otherwise it was a failure
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Ban",
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
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Ban",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    private fun sendEmail(email: String, message: String?) {
        val sendForm: SendForm
        if(!message.isNullOrBlank()) {
            sendForm = SendForm(
                    toAddresses = listOf(email),
                    fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                    subject = "Banned By Admin",
                    htmlBody = "<p>You have been banned. The reason for your ban is as follows: " +
                            "$message</p>"
            )
        }
        else {
            sendForm = SendForm(
                    toAddresses = listOf(email),
                    fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                    subject = "Banned By Admin",
                    htmlBody = "<p>You have been banned.</p>"
            )
        }

        awsSesSendService.execute(sendForm)
    }
}