package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.telescope.ITelescopeRepository

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
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

):Command<Long, Multimap<ErrorTag, String>>
{
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

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

        for (a:Appointment in apptPages)
        {
             var apptInfo = AppointmentInfo(a)
        }
        //success
        return SimpleResult(teleId, null)
    }
}