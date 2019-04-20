package com.radiotelescope.controller.admin.user

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.contracts.user.Unban
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle "unbanning" a User
 *
 * @param userWrapper the [UserUserWrapper]
 * @param awsSesSendService the [IAwsSesSendService] interface
 * @param logger the [Logger] service
 */
@RestController
class AdminUserUnbanController(
        private val userWrapper: UserUserWrapper,
        private val awsSesSendService: IAwsSesSendService,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the [PathVariable]
     * id and calling the [UserUserWrapper.unban] method. If this method
     * returns an [AccessReport] this means the user did not pass authentication
     * and the controller should respond accordingly.
     *
     * Otherwise, the [Unban] command object was executed, and the
     * controller should respond based on whether the command was a
     * success or not
     *
     * @param id the User id
     */
    @PutMapping(value = ["/api/users/{userId}/unban"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@PathVariable("userId") id: Long): Result {
        userWrapper.unban(id) {
            // If the command called after successful validation
            // is a success
            it.success?.let { theResponse ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Unban",
                                affectedRecordId = theResponse.id,
                                status = HttpStatus.OK.value()
                        )
                )

                sendEmail(theResponse.email)

                result = Result(data = id)
            }
            // Otherwise, it was an error
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Unban",
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
                            action = "User Unban",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    private fun sendEmail(email: String) {
        val sendForm = SendForm(
                toAddresses = listOf(email),
                fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                subject = "Unbanned By Admin",
                htmlBody = "<p>You have been unbanned.</p>"
        )
        awsSesSendService.execute(sendForm)
    }
}