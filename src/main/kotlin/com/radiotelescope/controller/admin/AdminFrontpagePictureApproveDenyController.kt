package com.radiotelescope.controller.admin

import com.radiotelescope.contracts.celestialBody.Retrieve
import com.radiotelescope.contracts.frontpagePicture.ApproveDeny
import com.radiotelescope.contracts.frontpagePicture.UserFrontpagePictureWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle admin Frontpage Picture approval and denial
 *
 * @param frontpagePictureWrapper the [UserFrontpagePictureWrapper]
 * @param context the [UserContext]
 * @param logger the [Logger] service
 */
@RestController
class AdminFrontpagePictureApproveDenyController(
        private val frontpagePictureWrapper: UserFrontpagePictureWrapper,
        private val context: UserContext,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method in charge of taking the frontpagePictureId [PathVariable]
     * and executing the [UserFrontpagePictureWrapper.approveDeny] method.
     * If this method returns an [AccessReport], this means they did not pass
     * authentication and the method will respond with errors.
     *
     * Otherwise, this means the [Retrieve] command was executed, and the controller
     * will check whether or not this command was a success or not, responding
     * appropriately.
     */
    @PutMapping(value = ["/api/frontpage-picture/{frontpagePictureId}"])
    fun execute(@PathVariable("frontpagePictureId") frontpagePictureId: Long,
                @RequestParam("isApprove") isApprove: Boolean) : Result {
        frontpagePictureWrapper.approveDeny(
                ApproveDeny.Request(
                        frontpagePictureId = frontpagePictureId,
                        isApprove = isApprove
                )
        ) {
            // If the command was a success
            it.success?.let { info ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.FRONTPAGE_PICTURE,
                                action = "Frontpage Picture Approval/Denial",
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
                                affectedTable = Log.AffectedTable.FRONTPAGE_PICTURE,
                                action = "Frontpage Picture Approval/Denial",
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
                            affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                            action = "Celestial Body Retrieval",
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