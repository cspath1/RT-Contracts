package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.info.AppointmentInfo
import com.radiotelescope.contracts.appointment.info.PointAppointmentInfo

/**
 * Override of the [Command] interface method used to retrieve [Appointment]
 * information
 *
 * @param appointmentId the requested Appointment's id
 * @param appointmentRepo the [IAppointmentRepository] interface
 */
class Retrieve(
        private val appointmentId: Long,
        private val appointmentRepo: IAppointmentRepository
) : Command<AppointmentInfo, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It checks to see if the
     * supplied id refers to an existing [Appointment] Entity, and if so,
     * it will retrieve it and adapt it to a [AppointmentInfo] data class.
     * It will then return this information in a [SimpleResult].
     *
     * If the appointment does not exist, it will return an error in the
     * [SimpleResult]
     */
    override fun execute(): SimpleResult<AppointmentInfo, Multimap<ErrorTag, String>> {
        if (!appointmentRepo.existsById(appointmentId)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Appointment Id #$appointmentId not found")
            return SimpleResult(null, errors)
        }

        val theAppointment = appointmentRepo.findById(appointmentId).get()
        // TODO: Change when other types are implemented
        val theInfo = PointAppointmentInfo(theAppointment)
        return SimpleResult(theInfo, null)
    }
}