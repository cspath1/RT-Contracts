package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.toAppointmentInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

/**
 * Command class to retrieve ALL appointments (past/present/future) from a telescope
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param telescopeId the Telescope id
 * @param pageRequest the [PageRequest]
 * @param telescopeRepo the [ITelescopeRepository] interface
 */
class RetrieveByTelescopeId(
        private var appointmentRepo: IAppointmentRepository,
        private var telescopeId: Long,
        private var pageRequest: PageRequest,
        private var telescopeRepo: ITelescopeRepository
): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {

        val apptPages: Page<Appointment> = appointmentRepo.retrieveAppointmentsByTelescopeId(telescopeId, pageRequest)
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!telescopeRepo.existsById(telescopeId)) {
            errors.put(ErrorTag.TELESCOPE_ID, "Telescope id $telescopeId not found")
            return SimpleResult(null, errors)
        }

        val infoPage = apptPages.toAppointmentInfoPage()
        return SimpleResult(infoPage, null)
    }
}