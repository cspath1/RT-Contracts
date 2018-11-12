package com.radiotelescope.controller.viewer

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.viewer.ViewerForm
import com.radiotelescope.controller.spring.Logger
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

class ViewerCreateController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
): BaseRestController(logger)
{
    /*
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/viewers/create"])

    fun execute(@RequestBody form: ViewerForm): Result
    {
        form.validateRequest()


    }

*/




}