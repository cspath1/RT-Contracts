package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.contracts.user.Unban
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle "unbanning" a User
 *
 * @param userWrapper the [UserUserWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserUnbanController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the [PathVariable]
     * id and calling the [UserUserWrapper.unban] method. If this method
     * returns an [AccessReport] this means the user did not pass authentication
     * and the controller should respond accordingly.
     *
     * Otherwise, the [Unban] command object was executed, and the
     * controller should respond based on whether the command was a
     * success or not
     *
     * @param id the User id
     */
    @PutMapping(value = ["/users/{userId}/unban"])
    fun execute(@PathVariable("userId") id: Long): Result {
        userWrapper.unban(id) { it ->
            // If the command called after successful validation
            // is a success
            it.success?.let {
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = Log.Action.UPDATE,
                                affectedRecordId = it
                        )
                )

                result = Result(data = it)
            }
            // Otherwise, it was an error
            it.error?.let {
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = Log.Action.UPDATE,
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap())
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = Log.Action.UPDATE,
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}