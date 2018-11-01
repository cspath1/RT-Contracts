package com.radiotelescope.controller

import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.service.RetrieveAuthUserService
import com.radiotelescope.security.UserSession
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller that will grab and return pertinent
 * user authentication information so that it can be
 * stored on login
 *
 * @param retrieveAuthUserService the [RetrieveAuthUserService] service
 * @param logger the [Logger] service
 */
@RestController
class AuthUserController(
        private val retrieveAuthUserService: RetrieveAuthUserService,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Calls the [RetrieveAuthUserService.execute] method that
     * will either return errors or a [UserSession] object,
     * depending on if the user is logged in or not
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/auth"])
    fun execute(): Result {
        // Execute the auth retrieve service
        val simpleResult = retrieveAuthUserService.execute()

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