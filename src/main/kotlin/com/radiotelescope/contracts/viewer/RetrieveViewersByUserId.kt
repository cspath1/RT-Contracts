package com.radiotelescope.contracts.viewer

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.viewer.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class RetrieveViewersByUserId(
        private val viewerRepo: IViewerRepository,
        private val userRepo: IUserRepository,
        private val sharing_user_id:Long,
        private val viewer_id:Long,
        private val pageable: Pageable
        ):Command<MutableList<Appointment>, Multimap<ErrorTag, String>> {

override fun execute(): SimpleResult<MutableList<Appointment>, Multimap<ErrorTag, String>> {

val errors = HashMultimap.create<ErrorTag, String>()

if (!userRepo.existsById(sharing_user_id))
{
    errors.put(ErrorTag.USER_ID, "user id ${sharing_user_id} does not exist")
    return SimpleResult(null, errors)
}
if (!userRepo.existsById(viewer_id))
{
    errors.put(ErrorTag.VIEWER_ID, "viewer with user id ${viewer_id} does not exist")
    return SimpleResult(null, errors)
}
    val viewers: Page<Viewer> = viewerRepo.getViewersOfAppointmentBySharingUserId(sharing_user_id, pageable)
    val sharedAppointments: MutableList<Appointment>? = arrayListOf()

    for (v in viewers)
    {
        sharedAppointments?.add(v.appointment!!)
    }
       return SimpleResult(sharedAppointments,null)
    }
}
