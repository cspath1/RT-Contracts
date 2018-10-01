package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UserUserWrapper
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

@RestController
class UserRegisterController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger) {
    @PostMapping(value = ["/users/register"])
    fun execute(@RequestBody form: RegisterForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let { 
            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise execute the factory command
        let { _ ->
            val simpleResult = userWrapper.factory(userPreconditionFailure()).register(
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
                result = Result(
                        errors = it.toStringMap()
                )
            }
        }

        return result
    }

    fun successLog(id: Long): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER,
                action = Log.Action.CREATE,
                timestamp = Date(),
                affectedRecordId = id
        )
    }
}