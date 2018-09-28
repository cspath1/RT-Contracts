package com.radiotelescope.repository.role

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface IUserRoleRepository : PagingAndSortingRepository<UserRole, Long>{
}