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
    fun findAllByUserId(userId: Long): List<UserRole>

    @Query(value = "SELECT * " +
            "FROM user_role " +
            "WHERE approved = '0'",
            countQuery = "SELECT COUNT(*) " +
                    "FROM user_role " +
                    "WHERE approved = '0' \\n#pageable\\n",
            nativeQuery = true)
    fun findNeedsApprovedUserRoles(pageable: Pageable): Page<UserRole>
}