package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

/**
 * Override of the [Command] interface method used for Appointment cancellation
 *
 * @param appointmentId the Appointment Id
 * @param appointmentRepo the [IAppointmentRepository] interface
 */
class Cancel(
        private val appointmentId: Long,
        private val appointmentRepo: IAppointmentRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Checks if the user exists.
     *
     * If user exists, it will retrieve past appointments by user id then it will return a [SimpleResult]
     * object with the [Appointment] id and a null errors field.
     *
     * If the user does not exist, it will return a [SimpleResult] with the errors and a
     * null success field
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = appointmentRepo.findById(appointmentId).get()
            theAppointment.status = Appointment.Status.CANCELED
            appointmentRepo.save(theAppointment)
            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Method in charge of handling constraints and validation
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!appointmentRepo.existsById(appointmentId)) {
            errors.put(ErrorTag.ID, "Appointment #$appointmentId was not found")
        } else {
            val theAppointment = appointmentRepo.findById(appointmentId).get()

            // If the appointment's status is either canceled or completed
            if (theAppointment.status == Appointment.Status.CANCELED) {
                errors.put(ErrorTag.STATUS, "Appointment #$appointmentId is already canceled")
            } else if (theAppointment.status == Appointment.Status.COMPLETED) {
                errors.put(ErrorTag.STATUS, "Appointment #$appointmentId is already completed")
            }
        }

        return if (errors.isEmpty) null else errors
    }
}