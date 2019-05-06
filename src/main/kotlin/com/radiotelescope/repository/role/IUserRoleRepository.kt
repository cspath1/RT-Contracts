package com.radiotelescope.repository.role

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository Interface for the [UserRole] Entity
 */
@Repository
interface IUserRoleRepository : PagingAndSortingRepository<UserRole, Long> {
    /**
     * Spring Repository method that will find all [UserRole] records for
     * a user
     *
     * @param userId the User id
     * @return a [List] of [UserRole] objects
     */
    fun findAllByUserId(userId: Long): List<UserRole>

    /**
     * Spring Repository method that will find all a [Page] of [UserRole]
     * that needs approval
     *
     * @param pageable the [Pageable] interface
     * @return a [Page] of [UserRole] objects
     */
    @Query(value = "SELECT * " +
            "FROM user_role " +
            "WHERE approved = '0'",
            countQuery = "SELECT COUNT(*) " +
                    "FROM user_role " +
                    "WHERE approved = '0'",
            nativeQuery = true)
    fun findNeedsApprovedUserRoles(pageable: Pageable): Page<UserRole>

    /**
     * Spring Repository method that will retrieve the membership role
     * (i.e. not USER) for a specific user.
     *
     * @param userId the User id
     * @return a [UserRole] or null, if the user has no membership role
     */
    @Query(value = "SELECT * " +
            "FROM user_role " +
            "WHERE approved = '1' AND role NOT IN ('USER') " +
            "AND user_id = ?1 " +
            "LIMIT 1",
            nativeQuery = true)
    fun findMembershipRoleByUserId(userId: Long): UserRole?

    /**
     * Spring Repository method that will retrieve all approved roles
     * for a specific user
     *
     * @param userId the User id
     * @return a list of [UserRole] object
     */
    @Query(value = "SELECT * " +
            "FROM user_role " +
            "WHERE approved = '1' " +
            "AND user_id = ?1",
            nativeQuery = true)
    fun findAllApprovedRolesByUserId(userId: Long): List<UserRole>
}