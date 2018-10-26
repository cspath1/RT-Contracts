package com.radiotelescope.controller.admin.user

import com.radiotelescope.contracts.user.Ban
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle an admin banning a user
 *
 * @param userWrapper the [UserUserWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminUserBanController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
): BaseRestController(logger) {
    /**
     * Execute method that is in charge of calling the [UserUserWrapper.ban]
     * method five then [userId] [PathVariable].
     *
     * If this method returns an [AccessReport], this means the user accessing the
     * endpoint did not pass authentication.
     *
     * Otherwise the [Ban] command was executed, and the controller should
     * respond based on wherther or not the command was a success or not
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PutMapping(value = ["api/users/{userId}/ban"])
    fun execute(@PathVariable("userId") userId: Long): Result {
        userWrapper.ban(id = userId) { it->
            // If the command was a success
            it.success?.let { id ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = Log.Action.BAN,
                                affectedRecordId = id
                        )
                )

                result = Result(data = id)
            }
            // Otherwise it was a failure
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = Log.Action.BAN,
                                affectedRecordId = null
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = Log.Action.BAN,
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}