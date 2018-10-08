package com.radiotelescope.controller.admin.role

import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.role.ValidateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AdminUserRoleValidateController(
        private val roleWrapper: UserUserRoleWrapper,
        logger: Logger
) : BaseRestController(logger) {
    @PostMapping(value = ["/users/roles/validate"])
    fun execute(@RequestBody validateForm: ValidateForm): Result {
        validateForm.validateRequest()?.let { errors -> 
            // Create error logs
            logger.createErrorLogs(
                    info = errorLog(), 
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        } ?: let { _ ->
            // Otherwise, call the wrapper command
            roleWrapper.validate(
                    request = validateForm.toRequest()
            ) {
                // If the request was a success
                it.success?.let { id -> 
                    // Create success log
                    logger.createSuccessLog(successLog(id))
                    result = Result(data = id)
                }
                // Otherwise it was a failure
                it.error?.let { errors ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = errorLog(),
                            errors = errors.toStringMap()
                    )
                    
                    result = Result(errors = errors.toStringMap())
                }
            }?.let {  
                // If we get here, user authentication failed
                // Create error logs
                logger.createErrorLogs(
                        info = errorLog(),
                        errors = it.toStringMap()
                )
            }
        }

        return result
    }

    override fun errorLog(): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER_ROLE,
                action = Log.Action.UPDATE,
                timestamp = Date(),
                affectedRecordId = null
        )
    }

    override fun successLog(id: Long): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER_ROLE,
                action = Log.Action.RETRIEVE,
                timestamp = Date(),
                affectedRecordId = id
        )
    }
}