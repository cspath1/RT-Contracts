package com.radiotelescope.controller.viewer

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.viewer.ViewerForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

class ViewerCreateController(
        private val userViewerWrapper: UserViewerWrapper,
        logger: Logger
): BaseRestController(logger)
{

    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/viewers/create/{sharingUserId}"])

    fun execute(@RequestBody form: ViewerForm): Result
    {
        form.validateRequest()?.let {
         logger.createErrorLogs(        info = Logger.createInfo(
                 affectedTable = Log.AffectedTable.VIEWER,
                 action = "Request for Viewer Creation",
                 affectedRecordId = null),
                 errors = it.toStringMap()
         )
            result = Result(errors = it.toStringMap())

        }?:
        let {
            userViewerWrapper.create(form.toRequest()){
                //fail case
                if (it.success == null)
                {
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.VIEWER,
                                    action = "Request for Viewer Creation",
                                    affectedRecordId = null
                            ),
                            errors = it.error!!.toStringMap()
                    )
                    result = Result(data = it.error)
                }
                //success case
                else
                {
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.VIEWER,
                                    action = "Request for Viewer Creation",
                                    affectedRecordId = it.success
                            )
                    )
                    result = Result(data = it.success)
                }
            }
        }
return result
    }
}