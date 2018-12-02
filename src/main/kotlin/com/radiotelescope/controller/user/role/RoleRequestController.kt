package com.radiotelescope.controller.user.role

import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.role.RequestRoleForm
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import com.radiotelescope.contracts.role.RequestRole
import com.radiotelescope.service.ses.IAwsSesSendService

/**
 * REST Controller to handle retrieval of User Roles for an Admin
 *
 * @param roleWrapper the [UserUserRoleWrapper]
 * @param awsSesSendService the [IAwsSesSendService]
 * @param userRepo the [IUserRepository]
 * @param logger the [Logger] service
 */
@RestController
class RoleRequestController (
        private val roleWrapper: UserUserRoleWrapper,
        private val userRepo: IUserRepository,
        private val awsSesSendService: IAwsSesSendService,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [RequestRoleForm]
     * into a [RequestRole.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserUserRoleWrapper.requestRole] method.
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/users/{userId}/role/request"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestParam(value = "role") role: UserRole.Role
    ): Result {
        val form = RequestRoleForm(
                userId = userId,
                role = role
        )
        // If any of the request params are null, respond with errors
        val errors = form.validateRequest()
        if(errors != null) {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER_ROLE,
                            action = "Request New User Role",
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val request = form.toRequest()
            roleWrapper.requestRole(request) { it ->
                //If the command was a success
                it.success?.let{
                    // Create success logs
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    Log.AffectedTable.USER_ROLE,
                                    action = "Request New User Role",
                                    affectedRecordId = it
                            )
                    )
                    result = Result(data = it)

                    sendEmail(userRepo.findAllAdminEmail())

                }
                // If the command was a failure
                it.error?.let{ errors ->
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    Log.AffectedTable.USER_ROLE,
                                    action = "Request New User Role",
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
                                Log.AffectedTable.USER_ROLE,
                                action = "Request New User Role",
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }

    private fun sendEmail(emails: List<String>) {
        val sendForm = SendForm(
                toAddresses = emails,
                fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                subject = "User Role Request",
                htmlBody = "<p>A new user role has been requested by a user and requires your approval.</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}