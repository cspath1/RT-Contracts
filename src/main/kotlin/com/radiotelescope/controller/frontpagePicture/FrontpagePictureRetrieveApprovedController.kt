package com.radiotelescope.controller.frontpagePicture

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
 * Rest Controller to handle retrieving all approved Frontpage Pictures
 *
 * @param frontpagePictureWrapper the [UserFrontpagePictureWrapper]
 * @param logger the [Logger] service
 */
@RestController
class FrontpagePictureRetrieveApprovedController(
        private val frontpagePictureWrapper: UserFrontpagePictureWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method in charge of listing all approved frontpage pictures
     *
     * Call the [UserFrontpagePictureWrapper.retrieveApproved] method.
     * If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/frontpage-picture/listApproved"])
    fun execute() : Result {
        val response = frontpagePictureWrapper.retrieveApproved().execute()

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

        return result
    }
}