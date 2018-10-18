package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.Appointment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract factory interface with methods for all [Appointment] CRUD operations
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
     * Abstract command used to retrieve appointment information
     *
     * @param id the Appointment's id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<AppointmentInfo, Multimap<ErrorTag,String>>

    /**
     * Abstract command used to retrieve a user's future appointments
     *
     * @param userId the User's id
     * @param pageable the [Pageable] interface
     * @return a [Command] object
     */
    fun getFutureAppointmentsForUser(userId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag,String>>


}