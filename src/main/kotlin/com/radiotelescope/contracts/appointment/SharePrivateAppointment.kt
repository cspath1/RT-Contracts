package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer
import org.springframework.data.domain.Page

class SharePrivateAppointment(
        private val appointmentRepo: IAppointmentRepository,
        private val viewerRepo: IViewerRepository,
        private val a_id: Long,
        private val userRepo: IUserRepository,
        private val user_id:Long,
        private val viewer_id:Long
        ):Command<Long, Multimap<ErrorTag, String>> {

    //step #1 is making a user a Viewer upon the request from the first user to share the first user's appointment
    //that should happen in SharePrivateAppointment

override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

val errors = HashMultimap.create<ErrorTag, String>()
    //That viewer_id actually should be the same as the actual user_id.
    //Which makes sense, because we're actually passing a Long into the userRepo.

    //If either one does not exist, then return with an error
if (!userRepo.existsById(user_id) || !userRepo.existsById(viewer_id))
{
    return SimpleResult(null , errors)
}

    val user = userRepo.findById(user_id)
    val appointment: Appointment = appointmentRepo.findById(a_id).get()

   val aInfo =  AppointmentInfo(appointment)


        //if I take the user_id of the user, and get from an endpoint, the user_id the user shares with
        //And then
        //Get the user id that I'm sharing WITH

        //The Viewer id is:
        //
       val v =  Viewer(viewer_id, user_id, a_id)
       //then the ViewerRepo comes into place

    //save it into the table
    viewerRepo.save(v)
    return SimpleResult(1,errors)
    }


}
