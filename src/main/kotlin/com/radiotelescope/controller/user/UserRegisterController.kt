package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.contracts.user.Register
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.user.RegisterForm
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * REST Controller to handle User Registration
 *
 * @param userWrapper the [UserUserWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserRegisterController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the incoming
     * [RegisterForm] object and seeing if it can be adapted to a
     * [Register.Request] object.
     *
     * If so, it will be adapted and the execute methof ror the
     * respective command will be called.
     *
     * @param form the [RegisterForm]
     */
    @PostMapping(value = ["/users/register"])
    fun execute(@RequestBody form: RegisterForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(errorLog(), it.toStringMap())

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise execute the wrapper command
        let { _ ->
            val simpleResult = userWrapper.register(
                    request = form.toRequest()
            ).execute()
            // If the command was a success
            simpleResult.success?.let {
                result = Result(
                        data = it
                )
                // Create a success log
                logger.createSuccessLog(successLog(it))
            }
            // Otherwise, it was a failure
            simpleResult.error?.let {
                // Create error logs
                logger.createErrorLogs(errorLog(), it.toStringMap())

                result = Result(
                        errors = it.toStringMap()
                )
            }
        }

        return result
    }

    /**
     * Override of the [BaseRestController.successLog] method that
     * returns a controller specific [Logger.Info]
     */
    override fun successLog(id: Long): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER,
                action = Log.Action.CREATE,
                timestamp = Date(),
                affectedRecordId = id
        )
    }

    /**
     * Override of the [BaseRestController.errorLog] method that
     * returns a controller specific [Logger.Info]
     */
    override fun errorLog(): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER,
                action = Log.Action.CREATE,
                timestamp = Date(),
                affectedRecordId = null
        )
    }
}