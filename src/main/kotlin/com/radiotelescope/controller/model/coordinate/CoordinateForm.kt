package com.radiotelescope.controller.model.coordinate

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.coordinate.CoordinateRequest
import com.radiotelescope.controller.model.BaseForm

/**
 * Data class containing all fields necessary for Coordinate creation
 *
 * @param hours the Right Ascension hours
 * @param minutes the Right Ascension minutes
 * @param declination the declination
 */
data class CoordinateForm(
        val hours: Int?,
        val minutes: Int?,
        val declination: Double?
) : BaseForm<CoordinateRequest> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [CoordinateRequest] object
     *
     * @return the [CoordinateRequest] object
     */
    override fun toRequest(): CoordinateRequest {
        return CoordinateRequest(
                hours = hours!!,
                minutes = minutes!!,
                declination = declination!!
        )
    }

    /**
     * Method that makes sure all required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (hours == null)
            errors.put(ErrorTag.HOURS, "Required field")
        if (minutes == null)
            errors.put(ErrorTag.MINUTES, "Required field")
        if (declination == null)
            errors.put(ErrorTag.DECLINATION, "Required field")

        return if (errors.isEmpty) null else errors
    }
}