package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.viewer.Create
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.viewer.Viewer

interface ViewerFactory
{
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>

    fun retrieveViewersByAppointmentId(appt_id: Long): Command<Long, Multimap<ErrorTag, String>>

    fun retrieveViewersByUserId(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>




}