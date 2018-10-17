package com.radiotelescope.repository.user

import com.radiotelescope.repository.appointment.Appointment
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [User] Entity
 */
@Repository
interface IUserRepository : PagingAndSortingRepository<User, Long> {
    /**
     * Spring Repository method that will see if any [User] records exist
     * with the given email parameter
     * @param email the email
     * @return true or false
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Spring Repository method that will grab a [User] by the email field
     * @param email the email
     * @return a [User]
     */
    fun findByEmail(email: String): User?


}