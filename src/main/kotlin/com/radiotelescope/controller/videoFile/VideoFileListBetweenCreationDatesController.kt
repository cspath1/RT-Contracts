package com.radiotelescope.controller.videoFile


import com.radiotelescope.contracts.videoFile.UserVideoFileWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.videoFile.ListBetweenCreationDatesForm
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * Rest Controller to handle retrieving a video file path
 *
 * @param logger the [Logger] service
 */
@RestController
class VideoFileListBetweenCreationDatesController(
        private val videoFileWrapper: UserVideoFileWrapper,
        logger: Logger
) : BaseRestController(logger) {
    @GetMapping(value = ["/api/video-files/listBetweenCreatedDates"])
    fun execute(@RequestBody form: ListBetweenCreationDatesForm) : Result {

        // If any of the request params are null, respond with errors
        val errors = form.validateRequest()
        if(errors != null) {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.VIDEO_FILE,
                            action = "Video File List Between Creation Times",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val request = form.toRequest()
            videoFileWrapper.listBetweenCreationDates(
                    request
            ) { response ->

                // If the command was a success
                response.success?.let { list ->
                    // Create success logs for each retrieval
                    list.forEach { info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        Log.AffectedTable.VIDEO_FILE,
                                        action = "Video File List Between Times",
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
                                    affectedTable = Log.AffectedTable.VIDEO_FILE,
                                    action = "Video File List Between Times",
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
                                affectedTable = Log.AffectedTable.VIDEO_FILE,
                                action = "Video File List Between Times",
                                affectedRecordId = null,
                                status = HttpStatus.FORBIDDEN.value()
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }
}