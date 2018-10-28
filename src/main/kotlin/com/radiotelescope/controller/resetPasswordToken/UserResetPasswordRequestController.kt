package com.radiotelescope.controller.resetPasswordToken

import com.radiotelescope.contracts.resetPasswordToken.UserResetPasswordTokenWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
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
        logger: Logger
): BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the incoming
     * email and pass it to the command to create reset password token
     *
     * @param email the user email
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/users/resetPasswordRequest"])
    fun execute(@RequestBody email: String): Result {
        let { _ ->
            val simpleResult = resetPasswordTokenWrapper.requestPasswordReset(
                    email = email
            ).execute()
            // If the command was a success
            simpleResult.success?.let {
                // TODO: Implement Emailing token to user

                // Create a success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.RESET_PASSWORD_TOKEN,
                                action = "Request Password Reset",
                                affectedRecordId = null
                        )
                )

                result = Result(data = it)
            }
            simpleResult.error?.let {
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.RESET_PASSWORD_TOKEN,
                                action = "Request Password Reset",
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap())
            }
        }

        return result
    }
}
