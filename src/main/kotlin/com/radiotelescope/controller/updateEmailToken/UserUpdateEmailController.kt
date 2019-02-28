package com.radiotelescope.controller.updateEmailToken

import com.radiotelescope.contracts.updateEmailToken.UserUpdateEmailTokenWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle User Update Email Token
 *
 * @param updateEmailTokenWrapper the [UserUpdateEmailTokenWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserUpdateEmailController (
        private val updateEmailTokenWrapper: UserUpdateEmailTokenWrapper,
        logger: Logger
) : BaseRestController(logger){
    /**
     * Execute method that is in charge of taking the token request param
     * and executing the [UserUpdateEmailTokenWrapper.updateEmail]
     * method that will update a user's email.
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PutMapping(value = ["/api/updateEmail"])
    fun execute(@RequestParam("token") token: String): Result {
        val simpleResult = updateEmailTokenWrapper.updateEmail(
                token = token
        ).execute()
        // if it was a success
        simpleResult.success?.let {
            // Create success logs
            logger.createSuccessLog(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Update Email",
                            affectedRecordId = it,
                            status = HttpStatus.OK.value()
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
                            action = "User Update Email",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        }

        return result
    }
}