package com.radiotelescope.controller.model.celestialBody

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.celestialBody.Create
import com.radiotelescope.contracts.celestialBody.ErrorTag
import com.radiotelescope.controller.model.BaseForm

/**
 * Create form that takes nullable versions of the [Create.Request] object.
 * It is in charge of making sure the required values are not null before
 * adapting into a [Create.Request] object
 *
 * @param name the Celestial Body's name
 * @param hours the Celestial Body's right ascension hours
 * @param minutes the Celestial Body's right ascension minutes
 * @param declination the Celestial Body's declination
 */
data class CreateForm(
        val name: String?,
        val hours: Int?,
        val minutes: Int?,
        val declination: Double?
) : BaseForm<Create.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Create.Request] object
     *
     * @return the [Create.Request] object
     */
    override fun toRequest(): Create.Request {
        return Create.Request(
                name = name!!,
                hours = hours,
                minutes = minutes,
                declination = declination
        )
    }

    /**
     * Makes sure all required fields are not null or blank
     *
     * @return a [HashMultimap] or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (name.isNullOrBlank())
            errors.put(ErrorTag.NAME, "Celestial Body Name may not be blank")

        return if (errors.isEmpty) null else errors
    }
}