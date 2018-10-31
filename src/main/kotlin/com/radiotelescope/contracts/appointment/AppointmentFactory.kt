package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.Appointment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * Abstract factory interface with methods for all [Appointment] Command objects
 */
interface AppointmentFactory {
    /**
     * Abstract command used to schedule an appointment
     *
     * @param request the [Create.Request] request
     * @return a [Command] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to cancel an appointment
     *
     * @param appointmentId the Appointment id
     * @return a [Command] object
     */
    fun cancel(appointmentId: Long): Command<Long, Multimap<ErrorTag,String>>

    /**
     * Abstract command used to retrieve appointment information
     *
     * @param id the Appointment's id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<AppointmentInfo, Multimap<ErrorTag,String>>

    /**
     * Abstract command user to retrieve completed appointments for a user
     *
     * @param userId the User id
     * @return a [Command] object
     */
    fun userCompletedList(userId: Long, pageable: Pageable): Command <Page<AppointmentInfo>, Multimap<ErrorTag,String>>

    /**
     * Abstract command used to update an appointment
     *
     * @param request the [Update.Request]
     * @return [Update] [Command] object
     */
    fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve all future appointments for a telescope
     *
     * @param telescopeId the Telescope id
     * @param pageable the [Pageable] interface
     * @return a [Command] object
     */
    fun retrieveFutureAppointmentsByTelescopeId(telescopeId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a user's future appointments
     *
     * @param userId the User's id
     * @param pageable the [Pageable] interface
     * @return a [Command] object
     */
    fun userFutureList(userId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag,String>>

    /**
     * Abstract command used to retrieve a user's future appointments
     *
     * @param startTime the start time of when to grab appointments
     * @param endTime the end time of when to grab the appointments
     * @param pageable the [Pageable] interface
     * @return a [Command] object
     */
    fun appointmentListBetweenDates(startTime: Date, endTime: Date, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag,String>>
}