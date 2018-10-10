package com.radiotelescope.contracts.appointment;

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.user.User
import org.springframework.data.domain.Pageable


/*
So for the Appointment entity we have the findByAppointmentId and findByUsernameId command objects
*/

interface AppointmentFactory
{

    fun create(request: Create.Request):Command<Long, Multimap<ErrorTag, String>>

    fun delete(id:Long):Command<Long, Multimap<ErrorTag,String>>

    fun retrieve(id:Long):Command<Long, Multimap<ErrorTag,String>>

    fun retrieveList(u: User, pageable: Pageable):Command <Long, Multimap<ErrorTag,String>>

    fun update(id: Long):Command<Long, Multimap<ErrorTag, String>>

}