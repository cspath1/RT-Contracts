package com.radiotelescope.controller

import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*
import javax.servlet.http.HttpServletResponse
import kotlin.collections.HashMap

/**
 * System-wide component that will handle any exception experienced
 * strictly within the radio telescope application (see 'basePackages')
 * so the front-end can properly handle whenever an exception occurs.
 * This will also log the exception so that admins can see that it occurred.
 *
 * @param logger the [Logger] service
 */
@ControllerAdvice(basePackages = ["com.radiotelescope"])
class ExceptionLogger(
        private val logger: Logger
) {
    /**
     * Method that will handle any exceptions. Creates a log of the exception and
     * returns something understandable to the end-user. Please note that normally,
     * when an unknown exception occurs, the front-end cannot uniformly handle it,
     * and this will allow that
     *
     * @param exception the [Exception]
     * @param response the [HttpServletResponse]
     * @return a [Result] object
     */
    @ExceptionHandler(Exception::class)
    fun logException(exception: Exception, response: HttpServletResponse): Result {
        response.status = HttpStatus.OK.value()

        // Create an errors map for the exception and the result
        val logErrors = HashMap<String, Collection<String>>()
        val resultErrors = HashMap<String, Collection<String>>()

        // Set the name and message based on if the values are null or not
        val exceptionName = if (exception::class.simpleName != null) exception::class.simpleName!! else "Exception"
        val exceptionMessage = if (exception.message != null) exception.message!! else "An Unknown Exception Occurred"

        logErrors[exceptionName] = listOf(exceptionMessage)

        // We don't want to return something the user won't understand
        // so return something generic for now
        resultErrors["EXCEPTION"] = listOf("An Unknown Exception Occurred")

        // Log the errors
        logger.createErrorLogs(
                info = Logger.Info(
                        affectedTable = null,
                        action = "Unknown",
                        timestamp = Date(),
                        affectedRecordId = null,
                        status = HttpStatus.INTERNAL_SERVER_ERROR.value()
                ),
                errors = logErrors
        )

        return Result(status = HttpStatus.BAD_REQUEST, errors = resultErrors.toMap())
    }
}