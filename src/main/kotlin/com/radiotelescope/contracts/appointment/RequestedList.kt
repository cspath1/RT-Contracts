package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toAppointmentInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface used to retrieve requested appointments.
 *
 * @param pageable the [Pageable] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class RequestedList(
        private val pageable: Pageable,
        private val appointmentRepo : IAppointmentRepository,
        private val userRepo : IUserRepository
) : Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method.
     *
     * It will create a [Page] of [AppointmentInfo] objects and
     * return this in the [SimpleResult.success] value.
     */
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        val page = appointmentRepo.findRequest(pageable)
        val infoPage = page.toAppointmentInfoPage()
        return SimpleResult(infoPage, null)
    }
}