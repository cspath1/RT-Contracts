package com.radiotelescope.contracts.viewer

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer


/**
 * Override of the [Command] interface method used for unsharing private appointment
 *
 * @param request the [UnsharePrivateAppointment.Request] object
 * @param viewerRepo the [IViewerRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param appointmentRepo the [IAppointmentRepository] interface */
class UnsharePrivateAppointment(
        private val request: Request,
        private val viewerRepo: IViewerRepository,
        private val userRepo: IUserRepository,
        private val appointmentRepo: IAppointmentRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraints checking and validation
     *
     * If validation passes, it will delete a [Viewer] object and
     * return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it)} ?: let {
            val viewer = viewerRepo.findByUserAndAppointment(request.userId, request.appointmentId)
            val id = viewer[0].id

            viewerRepo.delete(viewer[0])
            return SimpleResult(id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * UnsharePrivateAppointment request. It will ensure that both the user and appointment
     * id exists.
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        with(request) {
            if(!appointmentRepo.existsById(appointmentId))
                errors.put(ErrorTag.APPOINTMENT_ID, "Appointment #$appointmentId could not be found")
            if(!userRepo.existsById(userId))
                errors.put(ErrorTag.USER_ID, "User #$userId could not be found")
            if(userRepo.existsById(userId) && appointmentRepo.existsById(appointmentId) && !viewerRepo.isAppointmentSharedWithUser(userId, appointmentId))
                errors.put(ErrorTag.ID, "Appointment #$appointmentId is not shared with user #$userId")
        }
        return if(errors.isEmpty) null else errors
    }


    /**
     * Data class containing all the fields necessary for viewer deletion. Implement
     * the [BaseCreateRequest] interface.
     */
    data class Request(
            val userId: Long,
            val appointmentId: Long
    )
}