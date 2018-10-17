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
import org.springframework.data.domain.Pageable

class ListFutureAppointmentByUser(
        private val userId : Long,
        private val pageable: Pageable,
        private val appointmentRepo : IAppointmentRepository,
        private val userRepo : IUserRepository
) : Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if(!errors.isEmpty){
            return SimpleResult(null, errors)
        }

        val page = appointmentRepo.findFutureAppointmentsByUser(userId, pageable)
        val infoPage = page.toAppointmentInfoPage()
        return SimpleResult(infoPage, null)
    }

    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        // Check to see if user actually exists
        if(!userRepo.existsById(userId))
            errors.put(ErrorTag.USER_ID, "User Id #$userId could not be found")

        return errors
    }
}