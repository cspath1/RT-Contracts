package com.radiotelescope.controller.heartbeat

import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller with a single GET request used to ping the application
 *
 * @param logger the [Logger] service
 */
@RestController
class Ping(
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that returns 200 OK if the application is running.
     * This requires no authentication and is used for connection testing purposes.
     */
    @GetMapping(value = ["/api/ping"])
    fun execute(): Result {
        return result
    }
}