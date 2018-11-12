package com.radiotelescope.contracts.viewer

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer

class Create(
        private val request: Create.Request,
        private val viewerRepo: IViewerRepository,
        private val userRepo: IUserRepository
        ): Command<Long, Multimap<ErrorTag, String>>
{

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {


        //what if the user saved into the repo hasn't scheduled
       val errors = HashMultimap.create<ErrorTag, String>()
        with (request)
        {
            if (!userRepo.existsById(sharinguserId)) {
                errors.put(ErrorTag.USER_ID, "user id ${sharinguserId} does not exist")
                return SimpleResult(null, errors)
            }


            viewerRepo.save(request.toEntity())
            return SimpleResult(sharinguserId, null)
        }
    }


    data class Request(
        val viewerId:Long,
        val sharinguserId:Long,
        val appointment: Appointment
    ): BaseCreateRequest<Viewer>
    {
        override fun toEntity(): Viewer {
            return Viewer(
                    viewer_id = viewerId,
                    sharing_user_id = sharinguserId,
                    appointment = appointment
            )
        }

    }

}