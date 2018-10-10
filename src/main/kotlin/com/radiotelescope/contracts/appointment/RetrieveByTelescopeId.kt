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
import org.springframework.data.domain.Pageable

//Get appointments from a telescope
class RetrieveByTelescopeId(

        private var apptRepo: IAppointmentRepository,
        private var apptInfo: AppointmentInfo,
        private var teleId: Long,
        private var pageable: Pageable,
        private var userRepo: IUserRepository,
        private var userId: Long

        ):Command<Long, Multimap<ErrorTag, String>>
{

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

        var apptPages: Page<Appointment> = apptRepo.retrieveAppointmentsByTelescopeId(teleId, pageable)

        var errors = HashMultimap.create<ErrorTag, String>()

        if (!userRepo.existsById(userId)) {
            errors.put(ErrorTag.USER_ID, "User $userId not found")
            return SimpleResult(null, errors)
        }
        for (a:Appointment in apptPages)
        {
           apptInfo = AppointmentInfo(a)
        }

        //sucess
        return SimpleResult(teleId, null)


    }








}