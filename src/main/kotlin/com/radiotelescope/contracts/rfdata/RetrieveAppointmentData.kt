package com.radiotelescope.contracts.rfdata

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import com.radiotelescope.toInfoList

/**
 * Override of the [Command] interface used to retrieve an appointment's RF Data
 *
 * @param appointmentId the Appointment id
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param rfDataRepo the [IRFDataRepository] interface
 */
class RetrieveAppointmentData(
        private val appointmentId: Long,
        private val appointmentRepo: IAppointmentRepository,
        private val rfDataRepo: IRFDataRepository
) : Command<List<RFDataInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It checks to see if the [validateRequest]
     * method returned anything, and if so, it will return a [SimpleResult] with those
     * errors. Otherwise, it grabs the list of RF Data and adapts it to a list of
     * [RFDataInfo] objects, returning said list in the [SimpleResult]
     */
    override fun execute(): SimpleResult<List<RFDataInfo>, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theDataList = rfDataRepo.findRFDataForAppointment(appointmentId)
            return SimpleResult(theDataList.toInfoList(), null)
        }
    }

    /**
     * This method is in charge of constraint checking and validation. It checks
     * to see if the [appointmentId] refers to an existing record. It also checks to
     * see if the associated appointment has been completed or not.
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!appointmentRepo.existsById(appointmentId)) {
            errors.put(ErrorTag.APPOINTMENT_ID, "Appointment Id #$appointmentId could not be found")
            return errors
        }

        val theAppointment = appointmentRepo.findById(appointmentId).get()

        if (theAppointment.status != Appointment.Status.COMPLETED)
            errors.put(ErrorTag.APPOINTMENT_STATUS, "Appointment is not yet completed")

        return if (errors.isEmpty) null else errors
    }
}