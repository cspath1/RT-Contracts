package com.radiotelescope.controller.model.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.update.DriftScanAppointmentUpdate
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Update form that takes nullable versions of the [DriftScanAppointmentUpdate.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [DriftScanAppointmentUpdate.Request] object
 *
 * @param startTime the Appointment's new start time
 * @param endTime the Appointment's new end time
 * @param telescopeId the Appointment's new telescope id
 * @param isPublic whether the Appointment is to be public or not
 * @param priority the Appointment's new priority
 * @param elevation the Elevation
 * @param azimuth the Azimuth
 */
data class DriftScanAppointmentUpdateForm(
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        override val priority: Appointment.Priority?,
        val elevation: Double?,
        val azimuth: Double?
) : UpdateForm<DriftScanAppointmentUpdate.Request>() {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [DriftScanAppointmentUpdate.Request] object
     */
    override fun toRequest(): DriftScanAppointmentUpdate.Request {
        return DriftScanAppointmentUpdate.Request(
                id = -1L,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                priority = priority!!,
                elevation = elevation!!,
                azimuth = azimuth!!
        )

    }

    /**
     * Makes sure all required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (startTime == null)
            errors.put(ErrorTag.START_TIME, "Required field")
        if (endTime == null)
            errors.put(ErrorTag.END_TIME, "Required field")
        if (telescopeId == null)
            errors.put(ErrorTag.TELESCOPE_ID, "Required field")
        if (isPublic == null)
            errors.put(ErrorTag.PUBLIC, "Required field")
        if (priority == null)
            errors.put(ErrorTag.PRIORITY, "Required field")
        if (elevation == null)
            errors.put(ErrorTag.ELEVATION, "Required field")
        else if (elevation > 90 || elevation < 0)
            errors.put(ErrorTag.ELEVATION, "Elevation must be between 0 and 90")
        if (azimuth == null)
            errors.put(ErrorTag.AZIMUTH, "Required field")
        else if (azimuth >= 360 || azimuth < 0)
            errors.put(ErrorTag.AZIMUTH, "Azimuth must be between 0 and 360")

        return if (errors.isEmpty) null else errors
    }
}