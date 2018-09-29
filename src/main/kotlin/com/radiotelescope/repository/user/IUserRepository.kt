package com.radiotelescope.repository.user

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
     */
    fun existsByEmail(email: String): Boolean
}