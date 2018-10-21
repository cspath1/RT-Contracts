package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.telescope.ITelescopeRepository
import java.util.*

/**
 * Command class for editing an appointment
 * @param a_id of type [Long]
 * @param apptRepo of type [IAppointmentRepository]
 * @param updateRequest of type [Update.Request]
 * @param teleRepo of type [ITelescopeRepository]
 *
 */
class Update(
        private val request: Update.Request,
        private val appointmentRepo: IAppointmentRepository,
        private val telescopeRepo: ITelescopeRepository
):  Command<Long, Multimap<ErrorTag,String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will update and persist the [Appointment] object and
     * return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val appointment = appointmentRepo.findById(request.id).get()
        val updatedAppointment = appointmentRepo.save(request.updateEntity(appointment))
        return SimpleResult(updatedAppointment.id, null)
    }

    /**
     * Method responsible for constraint checking and validations for the appointment
     * update request. It will ensure the appointment and telescope exist.
     * It will ensure that the startTime is after the current time and
     * endTime is after the startTime.
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (appointmentRepo.existsById(id)) {
                if(telescopeRepo.existsById(telescopeId)) {
                    // TODO: Check for scheduling conflict later on
                    if (startTime.before(Date()))
                        errors.put(ErrorTag.START_TIME, "New start time cannot be before the current time")
                    if (endTime.before(startTime) || endTime == startTime)
                        errors.put(ErrorTag.END_TIME, "New end time cannot be less than or equal to the new start time")
                }
                else{
                    errors.put(ErrorTag.TELESCOPE_ID, "No Telescope was found with specified telescope Id: {$telescopeId}")
                    return errors
                }
            } else {
                errors.put(ErrorTag.ID, "No Appointment was found with specified Id: {$id}")
                return errors
            }
        }

        return errors
    }

    /**
     * Data class containing all fields necessary for appointment update. Implements the
     * [BaseUpdateRequest] interface and overrides the [BaseUpdateRequest.updateEntity]
     * method
     */
    data class Request(
            val id: Long,
            val telescopeId: Long,
            val startTime: Date,
            val endTime: Date
    ): BaseUpdateRequest<Appointment> {
        override fun updateEntity(appointment: Appointment): Appointment {
            appointment.telescopeId = telescopeId
            appointment.startTime = startTime
            appointment.endTime = endTime

            return appointment
        }
    }
}