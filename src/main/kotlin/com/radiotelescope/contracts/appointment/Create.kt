package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.repository.appointment.Appointment
import java.util.*

interface Create {
    abstract class Request : BaseCreateRequest<Appointment> {
        abstract val userId: Long
        abstract val startTime: Date
        abstract val endTime: Date
        abstract val telescopeId: Long
        abstract val isPublic: Boolean
    }
}