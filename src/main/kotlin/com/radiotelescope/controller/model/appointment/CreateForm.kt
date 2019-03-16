package com.radiotelescope.controller.model.appointment

import com.radiotelescope.controller.model.BaseForm
import java.util.*

abstract class CreateForm<REQUEST> : BaseForm<REQUEST> {
    abstract val userId: Long?
    abstract val startTime: Date?
    abstract val endTime: Date?
    abstract val telescopeId: Long?
    abstract val isPublic: Boolean?
}