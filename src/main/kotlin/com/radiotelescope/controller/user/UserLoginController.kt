package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.user.LoginForm
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserLoginController(
        private val userWrapper: UserUserWrapper
) : BaseRestController() {
    @PostMapping(value = ["/login"])
    fun execute(@RequestBody form: LoginForm): Result {
        form.validateRequest()?.let {
            result = Result(errors = it.toStringMap())
        } ?: let { _ ->
            val simpleResult = userWrapper.authenticate(
                    request = form.toRequest()
            ).execute()
            simpleResult.success?.let { result = Result(data = it) }
            simpleResult.error?.let { result = Result(errors = it.toStringMap()) }
        }

        return result
    }
}