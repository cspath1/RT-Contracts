package com.radiotelescope.repository.role

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository Interface for the [UserRole] Entity
 */
@Repository
interface IUserRoleRepository : PagingAndSortingRepository<UserRole, Long> {
    fun findAllByUserId(userId: Long): List<UserRole>
}