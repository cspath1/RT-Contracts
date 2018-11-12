package com.radiotelescope.controller.model.viewer

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.viewer.Create
import com.radiotelescope.contracts.viewer.ErrorTag
import com.radiotelescope.repository.appointment.Appointment

class ViewerForm(
        val viewer_id:Long?,
        val sharing_user_id:Long?,
        val appointment: Appointment?
)
{

    fun toRequest(
    ): Create.Request
    {
        return Create.Request(
                viewerId = viewer_id!!,
                sharinguserId = sharing_user_id!!,
                appointment = appointment!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>?
    {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (viewer_id == null)
        {
         errors.put(ErrorTag.VIEWER_ID, "Viewer id cannot be null")
        }

        else if (sharing_user_id == null)
        {
         errors.put(ErrorTag.USER_ID, "sharing user id cannot be null")
        }

        if (appointment == null)
        {
          errors.put(ErrorTag.APPOINTMENT_ID, "appointment does not exist")
        }

        //if validateRequest returns null, success
        if (errors.isEmpty()) return null

        else return errors
    }
}