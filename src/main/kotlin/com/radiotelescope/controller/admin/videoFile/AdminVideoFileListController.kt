package com.radiotelescope.controller.admin.videoFile

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.videoFile.ErrorTag
import com.radiotelescope.contracts.videoFile.UserVideoFileWrapper
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.BaseRestController
import org.springframework.stereotype.Controller
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AdminVideoFileListController(
        private val videoFileWrapper: UserVideoFileWrapper,
        logger: Logger
) : BaseRestController(logger){
    /**
     * Execute method that is in charge of returning a list of video files for admins.
     *
     * If the [pageNumber] or [pageSize] request parameters are null or invalid,
     * respond with errors. Otherwise, call the [UserVideoFileWrapper.retrieveList]
     * method. If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/videoFiles/retrieveList"])
    fun execute(@RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?) : Result {
        if ((pageNumber == null || pageNumber < 0) || (pageSize == null || pageSize <= 0)) {
            val errors = pageErrors()
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.VIDEO_FILE,
                            action = "User Request List Of Video Files",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        }
        else {
            val sort = Sort(Sort.Direction.ASC, "insert_timestamp")
            videoFileWrapper.retrieveList(PageRequest.of(pageNumber, pageSize, sort)) {
                // If the command was a success
                it.success?.let{ page ->
                    // Create success logs
                    page.content.forEach { info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(Log.AffectedTable.VIDEO_FILE,
                                        action = "Video File List",
                                        affectedRecordId = info.id,
                                        status = HttpStatus.OK.value()
                                )
                        )
                    }
                    result = Result(data = page)
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.VIDEO_FILE,
                                action = "Video File List",
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

    /**
     * Private method to return a [HashMultimap] of errors in the event
     * that the page size and page number are invalid
     */
    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}