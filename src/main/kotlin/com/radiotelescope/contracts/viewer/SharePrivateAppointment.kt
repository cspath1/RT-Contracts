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
 * Override of the [Command] interface method used for sharing private appointment
 *
 * @param request the [SharePrivateAppointment.Request] object
 * @param viewerRepo the [IViewerRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 */
class SharePrivateAppointment(
        private val request: Request,
        private val viewerRepo: IViewerRepository,
        private val userRepo: IUserRepository,
        private val appointmentRepo: IAppointmentRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation
     *
     * If validation passes, it will create and persist the [Viewer] object and
     * return the id in the the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theViewer = request.toEntity()

            theViewer.user = userRepo.findByEmail(request.email)!!
            theViewer.appointment = appointmentRepo.findById(request.appointmentId).get()

            viewerRepo.save(theViewer)
            return SimpleResult(theViewer.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * appointment sharePrivate request. It will ensure that both the user and appointment
     * id exists.
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        with(request) {
            if(!userRepo.existsByEmail(email))
                errors.put(ErrorTag.USER_ID, "User with email address ($email) could not be found")
            if(!appointmentRepo.existsById(appointmentId))
                errors.put(ErrorTag.ID, "Appointment #$appointmentId could not be found")
            if(appointmentRepo.existsById(appointmentId) && appointmentRepo.findById(appointmentId).get().isPublic)
                errors.put(ErrorTag.PRIVATE, "Appointment #$appointmentId is not private")
        }
        return if(errors.isEmpty) null else errors
    }
    /**
     * Data class containing all the fields necessary for viewer creation. Implement
     * the [BaseCreateRequest] interface.
     */
    data class Request(
            val email: String,
            val appointmentId: Long
    ) : BaseCreateRequest<Viewer> {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] mehtod that
         * returns a Viewer object
         */
        override fun toEntity(): Viewer {
            return Viewer()
        }
    }
}