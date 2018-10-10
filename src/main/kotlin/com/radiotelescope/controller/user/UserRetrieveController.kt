package com.radiotelescope.controller.user

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.Retrieve
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * REST Controller to handle retrieve User information
 *
 * @param userWrapper the [UserUserWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserRetrieveController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the id [PathVariable]
     * and making sure it is not null. If it is, respond with and error.
     *
     * Otherwise, execute the [UserUserWrapper.retrieve] method. If this
     * method returns an [AccessReport] respond with the errors. If not,
     * this means the [Retrieve] command was executed, and the execute
     * method should check if this was a success or not
     *
     * @param id the User's id
     */
    @GetMapping(value = ["/users/{id}/retrieve"])
    fun execute(@PathVariable("id") id: Long?): Result {
        // If the supplied path variable is not null, call the
        // retrieve
        id?.let { _ ->
            userWrapper.retrieve(id) { it ->
                // If the command called after successful validation is a
                // success
                it.success?.let {
                    // Create success logs
                    logger.createSuccessLog(successLog(it.id))
                    result = Result(data = it)
                }
                // Otherwise, it was an error
                it.error?.let {
                    // Create error logs
                    logger.createErrorLogs(errorLog(), it.toStringMap())
                    result = Result(errors = it.toStringMap())
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(errorLog(), it.toStringMap())
                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        } ?:
        // Otherwise, respond that the id was not valid
        let {
            // Create error logs
            val errors = idErrors()
            logger.createErrorLogs(errorLog(), errors.toStringMap())

            result = Result(errors = errors.toStringMap())
        }

        return result
    }

    /**
     * private method that returns a [HashMultimap] of errors
     * in the event the id passed in is null
     */
    private fun idErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.ID, "Invalid User Id")
        return errors
    }

    /**
     * Override of the [BaseRestController.successLog] method that
     * returns a controller specific [Logger.Info]
     */
    override fun successLog(id: Long): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER,
                action = Log.Action.RETRIEVE,
                timestamp = Date(),
                affectedRecordId = id
        )
    }

    /**
     * Override of the [BaseRestController.errorLog] method that
     * returns a controller specific [Logger.Info]
     */
    override fun errorLog(): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER,
                action = Log.Action.RETRIEVE,
                timestamp = Date(),
                affectedRecordId = null
        )
    }
}