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
 * @param apptRepo  the repository holding the appointments
 * @param teleId the id of the telescope from which you will retrieve its appointments
 * @pageRequest the pageRequest specifying a page(s) of appointments from this telescope
 * @teleRepo   the repository of telescopes
 */
class RetrieveByTelescopeId(
        private var apptRepo: IAppointmentRepository,
        private var teleId: Long,
        private var pageRequest: PageRequest,
        private var teleRepo: ITelescopeRepository

): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>>
{
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {

        val apptPages: Page<Appointment> = apptRepo.retrieveAppointmentsByTelescopeId(teleId, pageRequest)
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!teleRepo.existsById(teleId)) {
            errors.put(ErrorTag.TELESCOPE_ID, "Telescope id $teleId not found")
            return SimpleResult(null, errors)
        }
        val infoPage = apptPages.toAppointmentInfoPage()

        //success
        return SimpleResult(infoPage, null)
    }
}