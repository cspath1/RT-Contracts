package com.radiotelescope.controller.model.appointment.request

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.request.CelestialBodyAppointmentRequest
import java.util.*

/**
 * Request form that takes nullable versions of the [CelestialBodyAppointmentRequest.Request]
 * object. It is in charge of making sure these values are not null before adapting it to
 * a [CelestialBodyAppointmentRequest.Request] object.
 *
 * @param userId the User id
 * @param startTime the Appointment start time
 * @param endTime the Appointment end time
 * @param telescopeId the Telescope id
 * @param isPublic whether the Appointment is public or not
 * @param celestialBodyId the Celestial Body id
 */
data class CelestialBodyAppointmentRequestForm(
        override val userId: Long?,
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        val celestialBodyId: Long?
) : RequestForm<CelestialBodyAppointmentRequest.Request>() {
    /**
     * Override of the [RequestForm.toRequest] method that adapts the
     * form into a [CelestialBodyAppointmentRequest.Request] object
     *
     * @return the [CelestialBodyAppointmentRequest.Request] object
     */
    override fun toRequest(): CelestialBodyAppointmentRequest.Request {
        return CelestialBodyAppointmentRequest.Request(
                userId = userId!!,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                celestialBodyId = celestialBodyId!!
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (userId == null)
            errors.put(ErrorTag.USER_ID, "Invalid user id")
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