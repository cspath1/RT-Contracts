package com.radiotelescope.controller.model.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.manual.StartFreeControlAppointment
import com.radiotelescope.controller.model.BaseForm

/**
 * Form that takes nullable versions of the [StartFreeControlAppointment.Request] object.
 * It is in charge of making sure these values are not null before adapting it to a
 * [StartFreeControlAppointment.Request] object.
 *
 * @param userId the User id
 * @param telescopeId the Telescope id
 * @param duration the appointment duration in minutes
 * @param hours the Right Ascension hours
 * @param minutes the Right Ascension minutes
 * @param seconds the Right Ascension seconds
 * @param declination the Declination
 * @param isPublic whether the appointment is public or not
 */
data class StartFreeControlAppointmentForm(
        val userId: Long?,
        val telescopeId: Long?,
        val duration: Long?,
        val hours: Int?,
        val minutes: Int?,
        val seconds: Int?,
        val declination: Double?,
        val isPublic: Boolean?
) : BaseForm<StartFreeControlAppointment.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that adapts
     * the form into a [StartFreeControlAppointment.Request] object
     *
     * @return the [StartFreeControlAppointment.Request]
     */
    override fun toRequest(): StartFreeControlAppointment.Request {
        return StartFreeControlAppointment.Request(
                userId = userId!!,
                telescopeId = telescopeId!!,
                duration = duration!!,
                hours = hours!!,
                minutes = minutes!!,
                seconds = seconds!!,
                declination = declination!!,
                isPublic =  isPublic!!
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
            errors.put(ErrorTag.USER_ID, "Required field")
        if (telescopeId == null)
            errors.put(ErrorTag.TELESCOPE_ID, "Required field")
        if (duration == null)
            errors.put(ErrorTag.END_TIME, "Required field")
        if (hours == null)
            errors.put(ErrorTag.HOURS, "Required field")
        if (minutes == null)
            errors.put(ErrorTag.MINUTES, "Required field")
        if (seconds == null)
            errors.put(ErrorTag.SECONDS, "Required field")
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field")
        if (isPublic == null)
            errors.put(ErrorTag.PUBLIC, "Required field")

        return if (errors.isEmpty) null else errors
    }
}