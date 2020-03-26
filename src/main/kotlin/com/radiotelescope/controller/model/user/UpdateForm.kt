package com.radiotelescope.controller.model.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.Update
import com.radiotelescope.controller.model.BaseForm

/**
 * Update form that takes nullable versions of the [Update.Request] object.
 * It is in charge of making sure these values are not null before adapting
 * it into a [Update.Request] object
 *
 * @param id the User's id
 * @param firstName the User's new first name
 * @param lastName the User's new last name
 * @param phoneNumber the User's new phone number
 * @param company the User's new company
 */
data class UpdateForm(
        val id: Long?,
        val firstName: String?,
        val lastName: String?,
        val phoneNumber: String?,
        val company: String?,
        val profilePicture: String?,
        val notificationType: String?
) : BaseForm<Update.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that adapts
     * the form into [Update.Request] object
     */
    override fun toRequest(): Update.Request {
        return Update.Request(
                id = id!!,
                firstName = firstName!!,
                lastName = lastName!!,
                phoneNumber = phoneNumber,
                company = company,
                profilePicture = profilePicture,
                notificationType = notificationType!!
        )
    }

    /**
     * Makes sure all of the required fields are not null or blank
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if(id == null || id <= 0)
            errors.put(ErrorTag.ID, "ID may not be blank")
        if (firstName.isNullOrBlank())
            errors.put(ErrorTag.FIRST_NAME, "First Name may not be blank")
        if (lastName.isNullOrBlank())
            errors.put(ErrorTag.LAST_NAME, "Last Name may not be blank")
        if (notificationType.isNullOrBlank())
            errors.put(ErrorTag.NOTIFICATION_TYPE, "Notification Type may not be blank")
        return if (errors.isEmpty) null else errors
    }
}