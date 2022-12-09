package com.radiotelescope.controller.model.frontpagePicture

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.frontpagePicture.ErrorTag
import com.radiotelescope.contracts.frontpagePicture.Submit
import com.radiotelescope.controller.model.BaseForm

/**
 * Submit form that takes nullable versions of the [Submit.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * into a [Submit.Request] object.
 *
 * @param pictureTitle the picture title
 * @param pictureUrl the picture URL
 * @param description the picture's description
 * @param approved the picture's approval status
 */
class SubmitForm(
        val pictureTitle: String?,
        val pictureUrl: String?,
        val description: String?,
        val approved: Boolean?
) : BaseForm<Submit.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Submit.Request] object
     *
     * @return the [Submit.Request] object
     */
    override fun toRequest(): Submit.Request {
        return Submit.Request(
                pictureTitle = pictureTitle!!,
                pictureUrl = pictureUrl!!,
                description = description!!,
                approved = approved!!
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (pictureTitle == null)
            errors.put(ErrorTag.PICTURE_TITLE, "Required field")
        if (pictureUrl == null)
            errors.put(ErrorTag.PICTURE_URL, "Required field")
        if (description == null)
            errors.put(ErrorTag.DESCRIPTION, "Required field")
        if (approved == null)
            errors.put(ErrorTag.APPROVED, "Required field")

        return if (errors.isEmpty) null else errors
    }
}