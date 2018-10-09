package com.radiotelescope.controller.admin.role

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.role.ErrorTag
import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminRetrieveUserRoleController(
        private val roleWrapper: UserUserRoleWrapper,
        logger: Logger
) : BaseRestController(logger) {
    @GetMapping(value = ["/users/roles/{id}/retrieve"])
    fun execute(@PathVariable("id") id: Long?): Result {
        // If the supplied path variable is not null
        id?.let { _->
            roleWrapper.retrieve(id) { it ->
                // If the command was a success
                it.success?.let {
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.USER_ROLE,
                                    action = Log.Action.RETRIEVE,
                                    affectedRecordId = it.id
                            )
                    )

                    result = Result(data = it)
                }
                // Otherwise, it was a failure
                it.error?.let {
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.USER_ROLE,
                                    action = Log.Action.RETRIEVE,
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
                                affectedTable = Log.AffectedTable.USER_ROLE,
                                action = Log.Action.RETRIEVE,
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        } ?:
        // Otherwise, respond that the id was invalid
        let {
            val errors = idErrors()
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER_ROLE,
                            action = Log.Action.RETRIEVE,
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        }

        return result
    }

    private fun idErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.ID, "Invalid User Role id")
        return errors
    }
}