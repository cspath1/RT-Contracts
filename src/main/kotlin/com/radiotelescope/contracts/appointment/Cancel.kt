package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository

/**
 * Override of the [Command] interface method used for Assignment canceling
 *
 * @param apptId the appointment ID
 * @param apptRepo the [IAppointmentRepository] interface
 */
class Cancel
(private var apptId:Long,
 private val apptRepo: IAppointmentRepository
): Command<Long, Multimap<ErrorTag, String>>
{
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
        //Failure case: Appointment does not exist
        val errors = HashMultimap.create<ErrorTag, String>()
        if (!apptRepo.existsById(apptId))
        {
            errors.put(ErrorTag.ID, "appt with ID $apptId does not exist (attempted cancellation)")
            return SimpleResult(null, errors)
        }
        //Success case: Found appointment to cancel
        else
        {
            val appt:Appointment = apptRepo.findById(apptId).get()

            if (appt.status != Appointment.Status.Canceled && appt.status != Appointment.Status.Completed)
            appt.status = Appointment.Status.Canceled
            else {
                errors.put(ErrorTag.STATUS, "Cannot cancel an already canceled or completed appointment: appointment id is $apptId")
                return SimpleResult(null, errors)
            }

            apptRepo.save(appt)
            return SimpleResult(apptId, null)
        }
    }
}