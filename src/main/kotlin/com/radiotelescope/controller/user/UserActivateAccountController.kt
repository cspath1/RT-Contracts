package com.radiotelescope.controller.user

import com.radiotelescope.contracts.accountActivateToken.UserAccountActivateTokenWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle activate a User's account
 *
 */
@RestController
class UserActivateAccountController(
        private val activateTokenWrapper: UserAccountActivateTokenWrapper,
        logger: Logger
) : BaseRestController(logger) {
    fun execute(@RequestParam("token") token: String): Result {
        val simpleResult = activateTokenWrapper.activateAccount(
                token = token
        ).execute()
        // If it was a success
        simpleResult.success?.let {
            // Create success logs
            logger.createSuccessLog(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Account Activation",
                            affectedRecordId = it
                    )
            )

            result = Result(data = it)
        }
        // If it was a failure
        simpleResult.error?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Account Activation",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        }

        return result
    }
}