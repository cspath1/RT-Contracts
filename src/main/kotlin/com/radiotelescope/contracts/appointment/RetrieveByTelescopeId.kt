package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.telescope.ITelescopeRepository

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toAppointmentInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

//Get appointments from a telescope
class RetrieveByTelescopeId(
        private var apptRepo: IAppointmentRepository,
        private var teleId: Long,
        private var pageRequest: PageRequest,
        private var userRepo: IUserRepository,
        private var userId: Long,
        private var teleRepo: ITelescopeRepository

): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>>
{
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {

        //Add functionality to get future appointments by t_id that ARE NOT canceled (add new method to IAppointmentRepo)

        //When do I want all the appointments, and when do I only want the future appointments?

        //May actually want to make a separate class for it?

        //I could have a boolean flag: If true, get all appointments; if false, get only future appointments that aren't canceled

        var apptPages: Page<Appointment> = apptRepo.retrieveAppointmentsByTelescopeId(teleId, pageRequest)
        var errors = HashMultimap.create<ErrorTag, String>()

        if (!userRepo.existsById(userId)) {
            errors.put(ErrorTag.USER_ID, "User $userId not found")
            return SimpleResult(null, errors)
        }

        if (!teleRepo.existsById(teleId)) {
            errors.put(ErrorTag.TELESCOPE_ID, "Telescope id $teleId not found")
            return SimpleResult(null, errors)
        }
        val infoPage = apptPages.toAppointmentInfoPage()
        apptPages.forEach()
        {
           val info =  AppointmentInfo(it)
        }

        //success
        return SimpleResult(infoPage, null)
    }
}