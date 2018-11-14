package com.radiotelescope.contracts.viewer

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class RetrieveViewersByUserId(
        private val viewerRepo: IViewerRepository,
        private val userRepo: IUserRepository,
        private val request: Create.Request,
        private val pageable: Pageable
        ):Command<MutableList<Appointment>, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<MutableList<Appointment>, Multimap<ErrorTag, String>> {

        with(request)
        {
            val errors = HashMultimap.create<ErrorTag, String>()

            if (!userRepo.existsById(sharinguserId)) {
                errors.put(ErrorTag.USER_ID, "user id ${sharinguserId} does not exist")
                return SimpleResult(null, errors)
            }
            if (!userRepo.existsById(viewerId)) {
                errors.put(ErrorTag.VIEWER_ID, "viewer with user id ${viewerId} does not exist")
                return SimpleResult(null, errors)
            }



            //shared_appointment_id is apparently null
            val viewers: Page<Viewer> = viewerRepo.getViewersOfAppointmentBySharingUserId(sharinguserId, pageable)
            val sharedAppointments: MutableList<Appointment>? = arrayListOf()



            for (v in viewers) {
                sharedAppointments?.add(v.appointment!!)
            }
            return SimpleResult(sharedAppointments, null)
        }
    }
}
