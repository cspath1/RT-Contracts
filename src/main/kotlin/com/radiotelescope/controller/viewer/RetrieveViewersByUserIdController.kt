package com.radiotelescope.controller.viewer

import com.radiotelescope.contracts.appointment.Create
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.spring.Logger
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.CreateForm
import com.radiotelescope.controller.model.viewer.ViewerForm
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap

class RetrieveViewersByUserIdController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
): BaseRestController(logger) {
    /*
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/viewers/retrieve"])

    fun execute(@RequestBody form: ViewerForm):Result {
        //should return a multimap
        form.validateRequest()?.let()
        {
            if (it == null) {


                //here is where I would call the wrapper method
                //I need to add that wrapper method

                //do I actually need to make a new wrapper class?

            } else {
                //fail case
                //if it is not equal to null, then we have errors
                //populate the errors log

                //Logger.info type
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.VIEWER,
                                action = "Request to retrieve Viewers",
                                affectedRecordId = null
                        ),
                        errors = it.toStringMap()
                )

            }
        }
    }

    */
}