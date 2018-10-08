package com.radiotelescope.controller.admin.role

import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.contracts.role.Validate
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

/**
 * REST controller to handle validate a user's category of service
 */
@RestController
class AdminUserRoleValidateController(
        private val roleWrapper: UserUserRoleWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting a [ValidateForm]
     * to a [Validate.Request] command (if possible). Otherwise, it
     * will respond with errors.
     *
     * Once validated, it will call the [UserUserRoleWrapper.validate]
     * method, and respond accordingly
     *
     * @param validateForm the [ValidateForm] object
     */
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

    /**
     * Override of the [BaseRestController.errorLog] method that
     * returns a controller-specific [Logger.Info]
     */
    override fun errorLog(): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER_ROLE,
                action = Log.Action.UPDATE,
                timestamp = Date(),
                affectedRecordId = null
        )
    }

    /**
     * Override of the [BaseRestController.successLog] method that
     * returns a controller specific [Logger.Info]
     */
    override fun successLog(id: Long): Logger.Info {
        return Logger.Info(
                affectedTable = Log.AffectedTable.USER_ROLE,
                action = Log.Action.RETRIEVE,
                timestamp = Date(),
                affectedRecordId = id
        )
    }
}