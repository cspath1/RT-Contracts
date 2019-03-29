package com.radiotelescope.controller.model.appointment.create

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.create.DriftScanAppointmentCreate
import java.util.*

/**
 * Create form that takes nullable versions of the [DriftScanAppointmentCreate.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [DriftScanAppointmentCreate.Request] object
 *
 * @param userId the User id
 * @param startTime the Appointment start time
 * @param endTime the Appointment end time
 * @param telescopeId the Appointment's telescope
 * @param isPublic whether the Appointment is public or not
 * @param elevation the Elevation
 * @param azimuth the Azimuth

 */
data class DriftScanAppointmentCreateForm(
        override val userId: Long?,
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        val elevation: Double?,
        val azimuth: Double?
): CreateForm<DriftScanAppointmentCreate.Request>() {
    /**
     * Override of the [CreateForm.toRequest] method that
     * adapts the form into a [DriftScanAppointmentCreate.Request] object
     *
     * @return the [DriftScanAppointmentCreate.Request] object
     */
    override fun toRequest(): DriftScanAppointmentCreate.Request {
        return DriftScanAppointmentCreate.Request(
                userId = userId!!,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                elevation = elevation!!,
                azimuth = azimuth!!
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