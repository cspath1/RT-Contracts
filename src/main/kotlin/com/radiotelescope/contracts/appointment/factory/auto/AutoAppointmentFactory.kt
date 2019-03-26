package com.radiotelescope.contracts.appointment.factory.auto

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.create.AppointmentCreate
import com.radiotelescope.contracts.appointment.factory.AppointmentFactory
import com.radiotelescope.contracts.appointment.request.AppointmentRequest
import com.radiotelescope.contracts.appointment.request.CoordinateAppointmentRequest
import com.radiotelescope.contracts.appointment.update.AppointmentUpdate
import com.radiotelescope.contracts.appointment.update.CoordinateAppointmentUpdate

/**
 * Extension of the [AppointmentFactory] containing methods necessary for all appointments
 * that are conducted without user input (basically everything that isn't Free Control)
 */
interface AutoAppointmentFactory : AppointmentFactory {
    /**
     * Abstract command used to schedule an appointment
     *
     * @param request the [AppointmentCreate.Request] request
     * @return a [Command] object
     */
    fun create(request: AppointmentCreate.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to update an appointment
     *
     * @param request the [CoordinateAppointmentUpdate.Request]
     * @return [CoordinateAppointmentUpdate] [Command] object
     */
    fun update(request: AppointmentUpdate.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to request an appointment
     *
     * @param request the [CoordinateAppointmentRequest.Request] request
     * @return a [Command] object
     */
    fun request(request: AppointmentRequest.Request): Command<Long, Multimap<ErrorTag, String>>
}