package com.radiotelescope.controller.model.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.update.CelestialBodyAppointmentUpdate
import java.util.*

/**
 * Update form that takes nullable versions of the [CelestialBodyAppointmentUpdate.Request] object.
 * It is in charge of making sure these values are not null before adapting it to a
 * [CelestialBodyAppointmentUpdate.Request] object
 *
 * @param startTime the Appointment's new start time
 * @param endTime the Appointment's new end time
 * @param telescopeId the Appointment's new telescope id
 * @param isPublic whether the Appointment is public or not
 * @param celestialBodyId the Appointment's celestial body id
 */
data class CelestialBodyAppointmentUpdateForm(
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        val celestialBodyId: Long?
) : UpdateForm<CelestialBodyAppointmentUpdate.Request>() {
    /**
     * Override of the [UpdateForm.toRequest] method that adapts the
     * form into a [CelestialBodyAppointmentUpdate.Request] object
     */
    override fun toRequest(): CelestialBodyAppointmentUpdate.Request {
        return CelestialBodyAppointmentUpdate.Request(
                id = -1L,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                celestialBodyId = celestialBodyId!!
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
        if (celestialBodyId == null)
            errors.put(ErrorTag.CELESTIAL_BODY, "Required field")

        return if (errors.isEmpty) null else errors
    }
}