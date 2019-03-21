package com.radiotelescope.controller.model.appointment.request

import com.radiotelescope.controller.model.BaseForm
import java.util.*

abstract class RequestForm<REQUEST> : BaseForm<REQUEST> {
    abstract val userId: Long?
    abstract val startTime: Date?
    abstract val endTime: Date?
    abstract val telescopeId: Long?
    abstract val isPublic: Boolean?
}