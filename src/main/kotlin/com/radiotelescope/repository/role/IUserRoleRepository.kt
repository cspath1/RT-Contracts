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

    @Query(value = "SELECT * " +
            "FROM user_role " +
            "WHERE approved = '1' AND role NOT IN ('USER') " +
            "LIMIT 1",
            nativeQuery = true)
    fun findMembershipRoleByUserId(userId: Long): UserRole?
}