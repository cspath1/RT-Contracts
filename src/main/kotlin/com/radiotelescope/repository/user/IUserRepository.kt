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
     *
     * @param email the email
     * @return true or false
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Spring Repository method that will grab a [User] by the email field
     *
     * @param email the email
     * @return a [User]
     */
    fun findByEmail(email: String): User?

    /**
     * Spring Repository method that will return the number of [User] records
     * for a given account hash. This is used for generating a unique filepath
     * for each user for uploading a profile picture
     *
     * @param accountHash the Account Hash
     * @return the matching record count
     */
    fun countByAccountHash(accountHash: String): Int
}