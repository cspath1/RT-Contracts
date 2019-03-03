package com.radiotelescope.repository.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [User] Entity.
 */
@Repository
interface IUserRepository : PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {
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
    @Query(value = "SELECT u.email_address " +
            "FROM user u " +
            "JOIN user_role role ON u.id = role.user_id " +
            "WHERE role.role = 'ADMIN'",
            nativeQuery = true)
    fun findAllAdminEmail(): List<String>

    /**
     * Spring Repository method that will grab all non-admin users
     * NOTE: This method ignores user-role values of 'USER' since all
     * active users have this role, meaning it will remove duplicate values
     *
     * @param pageable a [Pageable] object
     * @return a [Page] of [User] objects
     */
    @Query(value = "SELECT DISTINCT u.* " +
            "FROM user u " +
            "JOIN user_role ON u.id = user_role.user_id " +
            "WHERE user_role.role NOT IN ('ADMIN', 'USER')",
            countQuery = "SELECT COUNT(*) " +
                    "FROM user u " +
                    "JOIN user_role ON u.id = user_role.user_id " +
                    "WHERE user_role.role NOT IN ('ADMIN', 'USER')",
            nativeQuery = true)
    fun findAllNonAdminUsers(pageable: Pageable): Page<User>
}