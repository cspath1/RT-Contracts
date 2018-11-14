package com.radiotelescope.contracts.viewer

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Command class to get all viewers of an Appointment by its ID.
 * @param viewerRepo [IViewerRepository] on which to call the query method to get the Viewers
   @param appointmentRepo [IAppointmentRepository] to make sure the appointment exists in the DB table
 * @param appointment_id [Long] the ID of the appointment of which we want the Viewers
 * @param pageable [Pageable] object to get a specific Page of Viewers
 * @return a Command object Command<MutableList<Appointment?>, Multimap<ErrorTag, String>>
 */

class RetrieveViewersByAppointmentId(

        val appointmentRepo: IAppointmentRepository,
        val viewerRepo: IViewerRepository,
        val appointment_id: Long,
        val pageable: Pageable

): Command<MutableList<Appointment?>, Multimap<ErrorTag, String>>
{
    override fun execute(): SimpleResult<MutableList<Appointment?>, Multimap<ErrorTag, String>>
    {

        val errors = HashMultimap.create<ErrorTag, String>()

        if (!appointmentRepo.existsById(appointment_id))
        {
            errors.put(ErrorTag.APPOINTMENT_ID, "Appointment ${appointment_id} does not exist")

            return SimpleResult(null, errors)
        }

        val page: Page<Viewer> = viewerRepo.getViewersByAppointmentId(appointment_id, pageable)
        val mutableListofAppointments: MutableList<Appointment?> = arrayListOf()

        for (p in page)
        {
        mutableListofAppointments.add(p.appointment!!)
        }
        return SimpleResult(mutableListofAppointments, null)
        }

    }



