package com.radiotelescope.controller.model.appointment.update

import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Abstract form for all Appointment Update endpoints. Extends the [BaseForm]
 *
 * @property startTime the Appointment start time
 * @property endTime the Appointment end time
 * @property telescopeId the Telescope id
 * @property isPublic whether the appointment is public or not
 * @property priority the Appointment priority
 */
abstract class UpdateForm<REQUEST> : BaseForm<REQUEST> {
    abstract val startTime: Date?
    abstract val endTime: Date?
    abstract val telescopeId: Long?
    abstract val isPublic: Boolean?
    abstract val priority: Appointment.Priority?
}