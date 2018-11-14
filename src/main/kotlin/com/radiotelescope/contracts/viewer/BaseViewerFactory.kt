package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import org.springframework.data.domain.Pageable

class BaseViewerFactory(
        private val viewerRepo: IViewerRepository,
        private val userRepo: IUserRepository,
        private val appointmentRepo: IAppointmentRepository

):ViewerFactory
{
    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Create(request, viewerRepo, userRepo)
    }

    override fun retrieveViewersByAppointmentId(appt_id: Long, pageable:Pageable): Command<MutableList<Appointment?>, Multimap<ErrorTag, String>> {
        return RetrieveViewersByAppointmentId(appointmentRepo, viewerRepo, appt_id,pageable)
    }
    override fun retrieveViewersByUserId(request: Create.Request, pageable: Pageable): Command<MutableList<Appointment>, Multimap<ErrorTag, String>>
    {
        return RetrieveViewersByUserId(viewerRepo, userRepo, request, pageable)
    }
}