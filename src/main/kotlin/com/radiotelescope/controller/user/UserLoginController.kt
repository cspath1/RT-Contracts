package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.Authenticate
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.user.LoginForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**\
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
     */
    @PostMapping(value = ["/login"])
    fun execute(@RequestBody form: LoginForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(errorLog(), it.toStringMap())

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise execute the wrapper command
        let { _ ->
            val simpleResult = userWrapper.authenticate(
                    request = form.toRequest()
            ).execute()
            // If the command was a success
            simpleResult.success?.let {
                result = Result(data = it)
                // Create a success log
                logger.createSuccessLog(successLog(it.id))
            }
            // Otherwise, it was a failure
            simpleResult.error?.let {
                // Create error logs
                logger.createErrorLogs(errorLog(), it.toStringMap())

                result = Result(errors = it.toStringMap())
            }
        }

        return result
    }

    // TODO - Make these abstract methods in the BestRestController

    fun successLog(id: Long): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER,
                action = Log.Action.LOG_IN,
                timestamp = Date(),
                affectedRecordId = id
        )
    }

    // TODO - Make these abstract methods in the BestRestController

    fun errorLog(): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER,
                action = Log.Action.CREATE,
                timestamp = Date(),
                affectedRecordId = null
        )
    }
}