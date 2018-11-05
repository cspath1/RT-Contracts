package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.contracts.user.ChangePassword
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import com.radiotelescope.controller.model.user.ChangePasswordForm
import org.springframework.web.bind.annotation.*

@RestController
class UserChangePasswordController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the [PathVariable]
     * request and calling the [UserUserWrapper.changePassword] method. If this method
     * returns an [AccessReport] this means the user did not pass authentication
     * and the controller should respond accordingly.
     *
     * Otherwise, the [ChangePassword] command object was executed, and the
     * controller should respond based on whether the command was a
     * success or not
     *
     * @param currentPassword the user's current password
     * @param password the user's request password to change to
     * @param passwordConfirm a confirmation of the user's requested password
     * @param userId the user's id
     */

    @PutMapping(value = ["/api/users/{userId}/changePassword"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@PathVariable("currentPassword") currentPassword: String,
                @PathVariable("password") password: String,
                @PathVariable("passwordConfirm") passwordConfirm : String,
                @PathVariable("userId") userId : Long): Result {
        userWrapper.changePassword(
                request = ChangePassword.Request(
                        currentPassword = currentPassword,
                        password = password,
                        passwordConfirm = passwordConfirm,
                        id = userId
                )

        ) { it->
            // If the command was a success
            it.success?.let { id ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User change password",
                                affectedRecordId = id
                        )
                )

                result = Result(data = id)
            }
            // Otherwise it was a failure
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User change password",
                                affectedRecordId = null
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
                            action = "User change password",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

}
