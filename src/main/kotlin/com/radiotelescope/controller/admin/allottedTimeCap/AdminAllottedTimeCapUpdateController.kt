package com.radiotelescope.controller.admin.allottedTimeCap

import com.radiotelescope.contracts.allottedTimeCap.Update
import com.radiotelescope.contracts.allottedTimeCap.UserAllottedTimeCapWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AccessReport
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.service.sns.IAwsSnsService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * Rest Controller used to update a user's allotted appointment time cap
 *
 * @param allottedTimeCapWrapper the [UserAllottedTimeCapWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminAllottedTimeCapUpdateController(
        private val allottedTimeCapWrapper: UserAllottedTimeCapWrapper,
        private val userRepo: IUserRepository,
        private val awsSesSendService: IAwsSesSendService,
        private val awsSnsService: IAwsSnsService,
        logger: Logger
): BaseRestController(logger){
    /**
     * Execute method that is in charge of calling the [UserAllottedTimeCapWrapper.update]
     * method five then [userId] [PathVariable].
     *
     * If this method returns an [AccessReport], this means the user accessing the
     * endpoint did not pass authentication.
     *
     * Otherwise the [Update] command was executed, and the controller should
     * respond based on whether or not the command was a success or not
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PutMapping(value = ["/api/users/{userId}/allotted-time"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestParam("allottedTime") allottedTime: Long?
    ): Result {
        allottedTimeCapWrapper.update(
                request = Update.Request(
                        userId = userId,
                        allottedTime = allottedTime
                )
        ) {
            // If the command was a success
            it.success?.let { timeCap ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.ALLOTTED_TIME_CAP,
                                action = "Update Allotted Time Cap",
                                affectedRecordId = timeCap.id,
                                status = HttpStatus.OK.value()
                        )
                )

                val theUser = userRepo.findById(userId).get()

                // Send an email or an SMS depending on the user's notification type
                if (theUser.notificationType == User.NotificationType.EMAIL ||
                        theUser.notificationType == User.NotificationType.ALL) {
                    sendEmail(theUser.email, allottedTime!!)
                }

                if (theUser.notificationType == User.NotificationType.SMS ||
                        theUser.notificationType == User.NotificationType.ALL) {
                    sendSms(theUser.phoneNumber!!, allottedTime!!)
                }

                result = Result(data = timeCap.id)
            }
            // Otherwise it was a failure
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.ALLOTTED_TIME_CAP,
                                action = "Update Allotted Time Cap",
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
                            affectedTable = Log.AffectedTable.ALLOTTED_TIME_CAP,
                            action = "Update Allotted Time Cap",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    private fun sendEmail(email: String, allottedTime: Long) {
        val sendForm = SesSendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <info@astroyork.com>",
                subject = "Allotted Time Cap Updated",
                htmlBody = "<p>Your allotted time cap has been updated to $allottedTime</p>"
        )
        awsSesSendService.execute(sendForm)
    }

    private fun sendSms(phoneNumber: String, allottedTime: Long) {
        val sendForm = SnsSendForm(
                toNumber = phoneNumber,
                topic = null,
                message = "Your allotted time cap has been updated to $allottedTime"
        )
        awsSnsService.send(sendForm)
    }
}