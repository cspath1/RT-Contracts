package com.radiotelescope.controller.viewer

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.spring.Logger
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.viewer.ViewerForm
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.RequestParam

class RetrieveViewersByUserIdController(
        private val userViewerWrapper: UserViewerWrapper,
        logger: Logger
): BaseRestController(logger) {

    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/viewers/retrieve/byUser/{sharingUserId}"])

    fun execute(@RequestBody form: ViewerForm,
                @RequestParam("page") pageNumber: Int?,
                @RequestParam("size") pageSize: Int?
    ): Result {

        if (pageNumber == null || pageSize == null || pageNumber < 0 || pageSize <= 0) {
            val errors = pageErrors()

            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.VIEWER,
                            action = "Request for Viewers By User Id",
                            affectedRecordId = null),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())

            return result
        }

        form.validateRequest()?.let {
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.VIEWER,
                            action = "Request for Viewers By User Id",
                            affectedRecordId = null),
                    errors = it.toStringMap()
            )
            result = Result(data = it.toStringMap())
        } ?: let {

            userViewerWrapper.getViewersByUserId(form.toRequest(), PageRequest.of(pageNumber, pageSize))
            {

                //fail case
                if (it.success == null) {
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.VIEWER,
                                    action = "Request to retrieve Viewers",
                                    affectedRecordId = null
                            ),
                            errors = it.error!!.toStringMap()
                    )
                    result = Result(data = it.error)
                } else {
                    //success case
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.VIEWER,
                                    action = "Request for Retrieve Viewers By Appointment Id",
                                    affectedRecordId = null
                            )
                    )
                }
            }
        }
        return result
    }

    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}