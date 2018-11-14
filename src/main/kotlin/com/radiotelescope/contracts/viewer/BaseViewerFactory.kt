package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.contracts.viewer.Create
import com.radiotelescope.contracts.viewer.ViewerFactory

class BaseViewerFactory(
        private val viewerRepo: IViewerRepository,
        private val userRepo: IUserRepository

):ViewerFactory
{

    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {

        return Create(request, viewerRepo, userRepo)
    }

    override fun retrieveViewersByAppointmentId(appt_id: Long): Command<Long, Multimap<ErrorTag, String>> {

        return RetrieveViewersByAppointmentId()
    }
    override fun retrieveViewersByUserId(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>
    {
        return RetrieveViewersByUserId(request, viewerRepo, userRepo)
    }
}