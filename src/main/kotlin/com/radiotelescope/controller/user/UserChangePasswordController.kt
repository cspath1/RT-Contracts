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
     * @param form the [ChangePasswordForm]
     */

    @PutMapping(value = ["/api/users/{userId}/changePassword"])
    @CrossOrigin(value = ["http://localhost:8081"])
    fun execute(@RequestBody form: ChangePasswordForm): Result {
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Change Password",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?: let { _ ->
            userWrapper.changePassword(
                    request = form.toRequest()
            ) { it ->
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

                // Set the errors depending on if the user was not authenticated or the
                // record did not exists
                result = if (it.missingRoles == null) {
                    Result(errors = it.invalidResourceId!!, status = HttpStatus.NOT_FOUND)
                }
                // user did not have access to the resource
                else {
                    Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
                }
            }
        }

        return result
    }

}
