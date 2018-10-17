package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import org.springframework.data.domain.PageRequest
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toAppointmentInfoPage
import org.springframework.data.domain.Page

class ListFutureAppointmentByUser(
        private val userId : Long,
        private val pageRequest: PageRequest,
        private val apptRepo : IAppointmentRepository,
        private val userRepo : IUserRepository
) : Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>>
{
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if(!errors.isEmpty){
            return SimpleResult(null, errors)
        }

        val page :Page<Appointment> = apptRepo.findFutureAppointmentsByUser(userId, pageRequest)
        val infoPage = page.toAppointmentInfoPage()
        return SimpleResult(infoPage, null)
    }


    private fun validateRequest(): Multimap<ErrorTag, String>
    {
        val errors = HashMultimap.create<ErrorTag, String>()

        // Check to see if user acutally exists
        if(!userRepo.existsById(userId))
            errors.put(ErrorTag.USER_ID, "There is no user with the id = $userId")

        if(pageRequest.pageNumber < 0)
            errors.put(ErrorTag.PAGE_PARAMS, "Page Number must not be less than 0")
        if(pageRequest.pageSize <= 0)
            errors.put(ErrorTag.PAGE_PARAMS, "Page Size must not be less or equal to 0")

        return errors
    }



}