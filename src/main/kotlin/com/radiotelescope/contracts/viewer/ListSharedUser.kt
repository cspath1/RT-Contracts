package com.radiotelescope.contracts.viewer

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toUserInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface used to retrieve an Appointment's
 * list of users that was shared with
 *
 * @param appointmentId the appointment's Id
 * @param pageable the [Pageable] interface
 * @param userRepo the [IUserRepository] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 */
class ListSharedUser (
        private val appointmentId: Long,
        private val pageable: Pageable,
        private val userRepo: IUserRepository,
        private val appointmentRepo: IAppointmentRepository
) : Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Checks if the appointment exists by
     * appointment's Id
     *
     * If appointment exists, it will create a [Page] of [UserInfo] objects and
     * return this in the [SimpleResult.success] value.
     *
     * If appointment does not exist, it will return the errors in a [SimpleResult.error]
     * value with a null success
     */
    override fun execute(): SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag, String>()

        return if (!appointmentRepo.existsById(appointmentId)) {
            errors.put(ErrorTag.APPOINTMENT_ID, "Appointment with id #$appointmentId does not exist")
            SimpleResult(null, errors)
        } else {
            val userPage = userRepo.findSharedUserByAppointment(appointmentId, pageable)
            val infoPage = userPage.toUserInfoPage()
            SimpleResult(infoPage, null)
        }
    }
}