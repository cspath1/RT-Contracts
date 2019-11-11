package com.radiotelescope.controller.videoFile

import com.radiotelescope.contracts.videoFile.UserVideoFileWrapper
import com.radiotelescope.contracts.videoFile.Create
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.controller.model.videoFile.CreateForm
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody

/**
 * REST Controller to handle Video File creation
 *
 * @param videoFileWrapper the [UserVideoFileWrapper]
 * @param logger the [Logger] service
*/
@RestController
class VideoFileCreateController(
        private val videoFileWrapper: UserVideoFileWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [CreateForm]
     * into a [Create.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserVideoFileWrapper.create] method.
     * Based on the result of this call, the method will either respond with
     * the data or the errors from the method call.
     */
    @PostMapping(value = ["/api/video-files"])
    fun execute(@RequestBody form: CreateForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.VIDEO_FILE,
                            action = "Video File Creation",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        }?:
        // Otherwise, execute the wrapper command
        let {
            val response = videoFileWrapper.create(
                    request = form.toRequest()
            ).execute()
            // If the command was a success
            response.success?.let { data ->
                // Create success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.VIDEO_FILE,
                                action = "Video File Creation",
                                affectedRecordId = data,
                                status = HttpStatus.OK.value()
                        )
                )
                result = Result(data = data)
            }
            // Otherwise, there was an error
            response.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.VIDEO_FILE,
                                action = "Video File Creation",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }

        return result
    }
}