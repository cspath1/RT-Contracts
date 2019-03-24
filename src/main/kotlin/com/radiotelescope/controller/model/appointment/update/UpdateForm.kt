package com.radiotelescope.controller.model.appointment.update

import com.radiotelescope.controller.model.BaseForm
import java.util.*

abstract class UpdateForm<REQUEST> : BaseForm<REQUEST> {
    abstract val startTime: Date?
    abstract val endTime: Date?
    abstract val telescopeId: Long?
    abstract val isPublic: Boolean?
}