package com.radiotelescope.controller

import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.service.RetrieveAuthAdminService
import com.radiotelescope.security.UserSession
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller used to determine if a user is logged in
 * and is an admin
 *
 * @param retrieveAuthAdminService the [RetrieveAuthAdminService] service
 * @param logger the [Logger] service
 */
@RestController
class AuthAdminController(
        private val retrieveAuthAdminService: RetrieveAuthAdminService,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Calls the [RetrieveAuthAdminService.execute] method that
     * will either return errors if a user is not logged in or
     * is not an admin or will return a [UserSession] object
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/authAdmin"])
    fun execute(): Result {
        // Execute the admin auth service
        val simpleResult = retrieveAuthAdminService.execute()

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