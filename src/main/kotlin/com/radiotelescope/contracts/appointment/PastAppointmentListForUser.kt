package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.toAppointmentInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface used to retrieve a User's
 * future appointments.
 * @param apptRepo the [IAppointmentRepository] interface
 * @param userId the user ID
 * @param userRepo the [IUserRepository] interface
 * @param pageRequest the [pageRequest] interface

 */

class PastAppointmentListForUser(
        private val appointmentRepo: IAppointmentRepository,
        private val userId: Long,
        private val userRepo: IUserRepository,
        private val pageRequest: PageRequest
):Command <Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Checks if the user exists by user id.
     *
     * If user exists it will create a [Page] of [AppointmentInfo] objects and
     * return this in the [SimpleResult.success] value.
     *
     * If user does not exist, it will will return the errors in a [SimpleResult.error]
     * value with a null success.
     */
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag, String>()

        return if (!userRepo.existsById(userId)) {
            errors.put(ErrorTag.USER_ID, "User with id $userId does not exist")
            SimpleResult(null, errors)
        } else {
            val appointmentPage = appointmentRepo.findPreviousAppointmentsByUser(userId, pageRequest)
            val infoPage = appointmentPage.toAppointmentInfoPage()
            SimpleResult(infoPage, null)
        }
    }
}