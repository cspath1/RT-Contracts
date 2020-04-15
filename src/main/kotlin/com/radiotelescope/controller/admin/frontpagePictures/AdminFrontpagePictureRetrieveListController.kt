package com.radiotelescope.controller.admin.frontpagePictures

import com.radiotelescope.contracts.frontpagePicture.UserFrontpagePictureWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle retrieving all Frontpage Pictures
 *
 * @param frontpagePictureWrapper the [UserFrontpagePictureWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AdminFrontpagePictureRetrieveListController(
        private val frontpagePictureWrapper: UserFrontpagePictureWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method in charge of listing all frontpage pictures
     *
     * Call the [UserFrontpagePictureWrapper.retrieveList] method.
     * If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/frontpage-picture/list"])
    fun execute() : Result {
        frontpagePictureWrapper.retrieveList { response ->
            // If the command was a success
            response.success?.let { list ->
                // Create success logs for each retrieval
                list.forEach { info ->
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    Log.AffectedTable.FRONTPAGE_PICTURE,
                                    action = "Frontpage Picture List",
                                    affectedRecordId = info.id,
                                    status = HttpStatus.OK.value()
                            )
                    )
                }

                result = Result(data = list)
            }
            // If the command was a failure
            response.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.FRONTPAGE_PICTURE,
                                action = "Frontpage Picture List",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
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
                            affectedTable = Log.AffectedTable.FRONTPAGE_PICTURE,
                            action = "Frontpage Picture List",
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