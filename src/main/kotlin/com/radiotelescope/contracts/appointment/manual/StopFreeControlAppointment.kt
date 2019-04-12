package com.radiotelescope.contracts.appointment.manual

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import java.util.*

/**
 * Override of the [Command] interface method used to end a
 * Free Control Appointment
 *
 * @param appointmentId the Appointment id
 * @param appointmentRepo the [IAppointmentRepository] interface
 */
class StopFreeControlAppointment(
        private val appointmentId: Long,
        private val appointmentRepo: IAppointmentRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will mark the appointment as completed and clear our
     * any remaining coordinates. It will then return the appointment id in the
     * [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = appointmentRepo.findById(appointmentId).get()

            // Clear out any remaining coordinates, mark it as completed,
            // and set the endTime to now
            theAppointment.coordinateList = mutableListOf()
            theAppointment.status = Appointment.Status.COMPLETED
            theAppointment.endTime = Date()
            appointmentRepo.save(theAppointment)

            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Validation method that ensures the appointment exists and is
     * currently an in progress free control appointment
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!appointmentRepo.existsById(appointmentId)) {
            errors.put(ErrorTag.ID, "Appointment #$appointmentId was not found")
            return errors
        }

        val theAppointment = appointmentRepo.findById(appointmentId).get()

        if (theAppointment.type != Appointment.Type.FREE_CONTROL)
            errors.put(ErrorTag.TYPE, "Appointment #$appointmentId is not a free control appointment")
        if (theAppointment.status != Appointment.Status.IN_PROGRESS)
            errors.put(ErrorTag.STATUS, "Appointment #$appointmentId is not In Progress")

        return if (errors.isEmpty) null else errors
    }
}

