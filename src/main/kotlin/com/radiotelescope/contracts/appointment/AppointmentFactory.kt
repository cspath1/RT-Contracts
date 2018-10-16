package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/**
 * Abstract factory interface with methods for all [Appointment] CRUD operations
 */
interface AppointmentFactory
{
    /**
     * Abstract command used to schedule an appointment
     *
     * @param request the [Create.Request] request
     * @return a [Command] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>

    fun cancel(a: Appointment, apptRepo: IAppointmentRepository, apptId: Long):Command<Long, Multimap<ErrorTag,String>>

    /**
     * Abstract command used to retrieve appointment information
     *
     * @param id the Appointment's id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<AppointmentInfo, Multimap<ErrorTag,String>>

    fun retrieveList(u: User, pageable: Pageable):Command <Long, Multimap<ErrorTag,String>>

    fun update(id: Long):Command<Long, Multimap<ErrorTag, String>>

    fun retrieveTelescopeById(id: Long, pageable:Pageable, user_id: Long): Command<Long, Multimap<ErrorTag, String>>

    fun getFutureAppointmentsForUser(userId: Long, pageRequest: PageRequest): Command<Page<AppointmentInfo>, Multimap<ErrorTag,String>>


}