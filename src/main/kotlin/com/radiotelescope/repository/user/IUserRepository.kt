package com.radiotelescope.repository.user

import org.springframework.data.jpa.repository.Query
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
     * @param email the email
     *
     * @return a [User]
     */
    fun findByEmail(email: String): User?

    /**
     * Spring Repository method that will grab all Admins' email
     *
     * @return a [List] of all admin emails
     */
    @Query(value = "SELECT user.email_address " +
            "FROM user " +
            "JOIN user_role ON user.id = user_role.user_id " +
            "WHERE user_role.role = 'ADMIN'",
            nativeQuery = true)
    fun findAllAdminEmail(): List<String>

}