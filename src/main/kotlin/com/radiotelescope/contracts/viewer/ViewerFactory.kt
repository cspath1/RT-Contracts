package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.viewer.Create
import com.radiotelescope.contracts.viewer.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.viewer.Viewer
import org.springframework.data.domain.Pageable

interface ViewerFactory
{
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>

    fun retrieveViewersByAppointmentId(appt_id: Long, pageable:Pageable): Command<MutableList<Appointment?>, Multimap<ErrorTag, String>>

    fun retrieveViewersByUserId(request: Create.Request, pageable:Pageable): Command<MutableList<Appointment>, Multimap<ErrorTag, String>>

}