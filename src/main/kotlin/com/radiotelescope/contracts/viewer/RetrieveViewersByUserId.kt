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

/**
 * Command class to get all viewers of a User.
 * @param viewerRepo [IViewerRepository] on which to call the query method to get the Viewers
 * @param userRepo [IUserRepository] to ensure the User and Viewer both exist in the DB tables
 * @param request [Create.Request] a Viewer Create Request, which will eventually come from the front-end
 * @param pageable [Pageable] object to get a specific Page of Viewers
 * @return a Command object [Command<MutableList<Appointment>, Multimap<ErrorTag, String>>]
 */

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

            val viewers: Page<Viewer> = viewerRepo.getViewersOfAppointmentBySharingUserId(sharinguserId, pageable)
            val sharedAppointments: MutableList<Appointment>? = arrayListOf()

            for (v in viewers) {
                sharedAppointments?.add(v.appointment!!)
            }
            return SimpleResult(sharedAppointments, null)
        }
    }
}
