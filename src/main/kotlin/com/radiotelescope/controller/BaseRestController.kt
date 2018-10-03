package com.radiotelescope.controller

import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger

abstract class BaseRestController(
        val logger: Logger
) {
    var result = Result()
}