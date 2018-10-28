package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toAppointmentInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface used to retrieve a User's
 * future appointments.
 *
 * @param userId the User id
 * @param pageable the [Pageable] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class UserFutureList(
        private val userId : Long,
        private val pageable: Pageable,
        private val appointmentRepo : IAppointmentRepository,
        private val userRepo : IUserRepository
) : Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes it will create a [Page] of [AppointmentInfo] objects and
     * return this in the [SimpleResult.success] value.
     *
     * If validation fails, it will will return the errors in a [SimpleResult.error]
     * value.
     */
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if(!errors.isEmpty){
            return SimpleResult(null, errors)
        }

        val page = appointmentRepo.findFutureAppointmentsByUser(userId, pageable)
        val infoPage = page.toAppointmentInfoPage()
        return SimpleResult(infoPage, null)
    }

    /**
     * Method responsible for constraint checking and validations that
     * ensures the user id refers to an existing User entity
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        // Check to see if user actually exists
        if(!userRepo.existsById(userId))
            errors.put(ErrorTag.USER_ID, "User Id #$userId could not be found")

        return errors
    }
}