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
 * Command class which calls IAppointmentRepository method to retrieve a page of future appointments by a telescope ID
 *
 * @param apptRepo of type [IAppointmentRepository] The repository of appointments
 * @param teleId of type [Long] The id of the telescope from which to retrieve appointments
 * @param pageRequest of type [PageRequest]  the pageRequest specifying a page(s) of appointments from this telescope
 * @param teleRepo of type  [ITelescopeRepository] The repository of telescopes
 *
 */

class RetrieveFutureAppointmentsByTelescopeId(

        private var apptRepo: IAppointmentRepository,
        private var teleId: Long,
        private var pageRequest: PageRequest,
        private var teleRepo: ITelescopeRepository

): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>>
{

    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        val apptPages: Page<Appointment> = apptRepo.retrieveFutureAppointmentsByTelescopeId(teleId, pageRequest)
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!teleRepo.existsById(teleId)) {
            errors.put(ErrorTag.TELESCOPE_ID, "Telescope id $teleId not found")
            return SimpleResult(null, errors)
        }
        val infoPage = apptPages.toAppointmentInfoPage()
        return SimpleResult(infoPage, null)
    }
}