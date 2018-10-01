package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.user.RegisterForm
import com.radiotelescope.controller.model.Result
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRegisterController(
        private val userWrapper: UserUserWrapper
) : BaseRestController() {
    @PostMapping(value = ["/users/register"])
    fun execute(@RequestBody form: RegisterForm): Result {
        form.validateRequest()?.let { 
            result = Result(errors = it.toStringMap())
        } ?: let { _ ->
            val simpleResult = userWrapper.factory(userPreconditionFailure()).register(
                    request = form.toRequest()
            ).execute()
            simpleResult.success?.let { result = Result(it) }
            simpleResult.error?.let { result = Result(it) }
        }

        return result
    }
}