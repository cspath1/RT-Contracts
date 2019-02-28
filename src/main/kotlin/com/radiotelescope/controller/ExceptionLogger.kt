package com.radiotelescope.controller

import com.radiotelescope.controller.spring.Logger
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice(basePackages = ["com.radiotelescope"])
class ExceptionLogger(
        private val logger: Logger
) {
    @ExceptionHandler(Exception::class)
    fun logException(exception: Exception) {

    }
}