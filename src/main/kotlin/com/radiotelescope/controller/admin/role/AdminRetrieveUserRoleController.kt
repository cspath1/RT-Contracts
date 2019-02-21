package com.radiotelescope.controller.admin.role

import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle retrieval of User Roles for an Admin
 *
 * @param roleWrapper the [UserUserRoleWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminRetrieveUserRoleController(
        private val roleWrapper: UserUserRoleWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the incoming
     * id [PathVariable] and calling the [UserUserRoleWrapper.retrieve]
     * method. If this method returns an [AccessReport], that means the user
     * did not pass authentication.
     *
     * Otherwise, the command was executed and the controller should respond
     * back to the client accordingly based on if it was a success or an error
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/roles/{roleId}"])
    fun execute(@PathVariable("roleId") roleId: Long): Result {
        roleWrapper.retrieve(roleId) {
            // If the command was a success
            it.success?.let { info ->
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER_ROLE,
                                action = "Retrieve",
                                affectedRecordId = info.id
                        )
                )

                result = Result(data = info)
            }
            // Otherwise, it was a failure
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER_ROLE,
                                action = "Retrieve",
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
                            affectedTable = Log.AffectedTable.USER_ROLE,
                            action = "Retrieve",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}