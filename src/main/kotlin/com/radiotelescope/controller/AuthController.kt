package com.radiotelescope.controller

import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.service.RetrieveAuthService
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
        private val retrieveAuthService: RetrieveAuthService,
        logger: Logger
) : BaseRestController(logger) {
    @GetMapping(value = ["/api/auth"])
    fun execute(): Result {
        // Execute the auth retrieve service
        val simpleResult = retrieveAuthService.execute()

        // If the service was a success
        simpleResult.success?.let {
            result = Result(data = it)
        }

        // Otherwise it was a failure
        simpleResult.error?.let {
            result = Result(errors = it.toStringMap())
        }

        return result
    }
}