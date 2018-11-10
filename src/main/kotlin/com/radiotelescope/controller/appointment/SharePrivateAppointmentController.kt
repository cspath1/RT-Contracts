package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.spring.Logger

class SharePrivateAppointmentController(
        private val userAppointmentWrapper: UserAppointmentWrapper,
        logger: Logger

):BaseRestController(logger)
{

}