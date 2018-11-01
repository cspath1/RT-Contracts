package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

/**
 * Command class for editing an appointment
 *
 * @param appointmentId the appointment Id
 * @param appointmentRepo of type [IAppointmentRepository]
 */
class MakePublic (
        private val appointmentId: Long,
        private val appointmentRepo: IAppointmentRepository
): Command<Long, Multimap<ErrorTag, String>> {
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

        if(!errors.isEmpty)
            return SimpleResult(null, errors)

        val appointment = appointmentRepo.findById(appointmentId).get()
        appointment.isPublic = true
        appointmentRepo.save(appointment)

        return SimpleResult(appointment.id, null)
    }

    /**
     * Method responsible for constraint checking and validations for the appointment
     * parameters. It will ensure the appointment exist and that the appointment is not
     * already public.
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        if(!appointmentRepo.existsById(appointmentId))
            errors.put(ErrorTag.ID, "Appointment does not exist with specified Id")
        else if(appointmentRepo.findById(appointmentId).get().isPublic)
            errors.put(ErrorTag.PUBLIC, "Appointment is already public")

        return errors
    }
}