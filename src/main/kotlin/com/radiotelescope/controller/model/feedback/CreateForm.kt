package com.radiotelescope.controller.model.feedback

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.feedback.Create
import com.radiotelescope.contracts.feedback.ErrorTag
import com.radiotelescope.controller.model.BaseForm

/**
 * Create form that takes nullable versions of the [Create.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * into a [Create.Request] object.
 *
 * @param name the user's name
 * @param priority the feedback's priority
 * @param comments the feedback's comment
 */
data class CreateForm(
        val name: String?,
        val priority: Int?,
        val comments: String?
) : BaseForm<Create.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Create.Request] object
     *
     * @return the [Create.Request] object
     */
    override fun toRequest(): Create.Request {
        return Create.Request(
                name = name,
                priority = priority!!,
                comments = comments!!
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (priority == null)
            errors.put(ErrorTag.PRIORITY, "Required field")
        if (comments == null)
            errors.put(ErrorTag.COMMENTS, "Required field")

        return if (errors.isEmpty) null else errors
    }
}