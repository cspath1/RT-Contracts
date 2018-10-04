package com.radiotelescope.controller

import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger

/**
 * Abstract class that all REST controllers will extend
 * that takes a [Logger] as a parameter so we can handle
 * logging successes and errors. It also has a [Result]
 * object that is used to return information to the client
 */
abstract class BaseRestController(
        val logger: Logger
) {
    var result = Result()
}