package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer

class SharePrivate(
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

            theViewer.user = userRepo.findById(request.userId).get()
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
        var errors = HashMultimap.create<ErrorTag, String>()
        with(request) {
            if(!userRepo.existsById(userId))
                errors.put(ErrorTag.USER_ID, "User #$userId could not be found")
            if(!appointmentRepo.existsById(appointmentId))
                errors.put(ErrorTag.ID, "Appointment #$appointmentId could not be found")
            if(appointmentRepo.existsById(appointmentId))
                if(appointmentRepo.findById(appointmentId).get().isPublic)
                    errors.put(ErrorTag.PUBLIC, "Appointment #$appointmentId is not private")
        }
        return if(errors.isEmpty) null else errors
    }
    /**
     * Data class containing all the fields necessary for viewer creation. Implement
     * the [BaseCreateRequest] interface.
     */
    data class Request(
            val userId: Long,
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