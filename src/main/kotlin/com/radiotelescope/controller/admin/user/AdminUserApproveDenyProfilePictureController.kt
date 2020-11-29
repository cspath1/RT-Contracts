package com.radiotelescope.controller.admin.user

import com.radiotelescope.contracts.user.ApproveDeny
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle admin Frontpage Picture approval and denial
 *
 * @param userWrapper the [UserUserWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminUserApproveDenyProfilePictureController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
): BaseRestController(logger) {
    /**
     * Execute method in charge of taking the userId [PathVariable]
     * and executing the [UserUserWrapper.approveDenyProfilePicture] method.
     * If this method returns an [AccessReport], this means they did not pass
     * authentication and the method will respond with errors.
     *
     * Otherwise, this means the [ApproveDeny] command was executed, and the controller
     * will check whether or not this command was a success or not, responding
     * appropriately.
     */
    @PostMapping(value = ["/api/users/{userId}/profile-picture"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestParam("isApprove") isApprove: Boolean) : Result {
        userWrapper.approveDenyProfilePicture(
                ApproveDeny.Request(
                        userId = userId,
                        approved = isApprove
                )
        ) {
            // If the command was a success
            it.success?.let { info ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "Profile Picture Approval/Denial",
                                affectedRecordId = info.id,
                                status = HttpStatus.OK.value()
                        )
                )

                result = Result(data = info)
            }
            // Otherwise, it was an error
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "Profile Picture Approval/Denial",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }?.let {
            // If we get here, that means the user did not pass authentication
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "Profile Picture Approval/Denial",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}