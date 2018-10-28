package com.radiotelescope.controller.resetPasswordToken

import com.radiotelescope.contracts.resetPasswordToken.UserResetPasswordTokenWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    fun execute(@RequestParam email: String){
        let { _ ->
            val simpleResult = resetPasswordTokenWrapper.resetPasswordToken(
                    email = email
            ).execute()
            // If the command was a success
            simpleResult.success?.let {
                // TODO: Implement Emailing token to user

                // Create a success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.RESET_PASSWORD_TOKEN,
                                action = "User Request Password Reset",
                                affectedRecordId = null
                        )
                )
            }
            simpleResult.error?.let {
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.RESET_PASSWORD_TOKEN,
                                action = "User Request Password Reset",
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )
            }
        }
    }
}
