package com.radiotelescope.contracts.appointment.factory.manual

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.factory.AppointmentFactory
import com.radiotelescope.contracts.appointment.manual.AddFreeControlAppointmentCommand
import com.radiotelescope.contracts.appointment.manual.StartFreeControlAppointment

/**
 * Extension of the [AppointmentFactory] containing methods necessary for all appointments
 * that allow for manual control.
 */
interface ManualAppointmentFactory : AppointmentFactory {
    /**
     * Abstract command used to start a manual appointment
     *
     * @param request the [StartFreeControlAppointment.Request] request
     * @return a [Command] object
     */
    fun startAppointment(request: StartFreeControlAppointment.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to add a command to a manual appointment
     *
     * @param request the [AddFreeControlAppointmentCommand.Request] object
     * @return a [Command] object
     */
    fun addCommand(request: AddFreeControlAppointmentCommand.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to end a manual appointment
     *
     * @param appointmentId the Appointment id
     * @return a [Command] object
     */
    fun stopAppointment(appointmentId: Long): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to calibrate a manual appointment
     */
    fun calibrateAppointment(appointmentId: Long): Command<Long, Multimap<ErrorTag, String>>
}