package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.Authenticate
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.user.LoginForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle User Login
 *
 * @param userWrapper the [UserUserWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserLoginController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the incoming
     * [LoginForm] object and seeing if it can be adapted to a
     * [Authenticate.Request] object.
     *
     * If so, it will be adapted and the execute method for the
     * respective command will be called
     *
     * @param form the [LoginForm]
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/login"])
    fun execute(@RequestBody form: LoginForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Login",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise execute the wrapper command
        let {
            val simpleResult = userWrapper.authenticate(
                    request = form.toRequest()
            ).execute()
            // If the command was a success
            simpleResult.success?.let { data ->
                result = Result(data = data)

                // Create a success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Login",
                                affectedRecordId = data.id,
                                status = HttpStatus.OK.value()
                        )
                )
            }
            // Otherwise, it was a failure
            simpleResult.error?.let { error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Login",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = error.toStringMap()
                )

                result = Result(errors = error.toStringMap())
            }
        }

        return result
    }
}