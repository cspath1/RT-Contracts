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
import org.springframework.data.domain.Pageable

/**
 * Command class which calls IAppointmentRepository method to retrieve
 * a page of future appointments by a telescope ID
 *
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param telescopeId the Telescope id
 * @param pageable the [Pageable] interface
 * @param telescopeRepo the [ITelescopeRepository] interface
 *
 */
class RetrieveFutureAppointmentsByTelescopeId(
        private val appointmentRepo: IAppointmentRepository,
        private val telescopeId: Long,
        private val pageable: Pageable,
        private val telescopeRepo: ITelescopeRepository
): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. If the telescope exists, it will
     * retrieve a [Page] of future appointments, adapt it into a [Page] of
     * [AppointmentInfo] objects, and respond with it in the [SimpleResult].
     *
     * Otherwise, it will return an error in the [SimpleResult] that the
     * id could not be found.
     */
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        val appointmentPage: Page<Appointment> = appointmentRepo.retrieveFutureAppointmentsByTelescopeId(telescopeId, pageable)
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!telescopeRepo.existsById(telescopeId)) {
            errors.put(ErrorTag.TELESCOPE_ID, "Telescope id $telescopeId not found")
            return SimpleResult(null, errors)
        }

        val infoPage = appointmentPage.toAppointmentInfoPage()
        return SimpleResult(infoPage, null)
    }
}