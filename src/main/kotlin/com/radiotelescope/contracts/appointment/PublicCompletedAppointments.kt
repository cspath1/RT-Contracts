package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.toAppointmentInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface used to retrieve a public completed
 * appointments.
 *
 * @param pageable the [Pageable] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 */
class PublicCompletedAppointments(
        private val pageable: Pageable,
        private val appointmentRepo: IAppointmentRepository
) : Command<Page<AppointmentInfo>, ErrorTag> {
    /**
     * Override of the [Command.execute] method. It will call the
     * [IAppointmentRepository.findCompletedPublicAppointments] method
     * which will return be adapted to return a Page of [AppointmentInfo]
     * objects
     */
    override fun execute(): SimpleResult<Page<AppointmentInfo>, ErrorTag> {
        val appointmentPage = appointmentRepo.findCompletedPublicAppointments(pageable)
        return SimpleResult(appointmentPage.toAppointmentInfoPage(), null)
    }
}