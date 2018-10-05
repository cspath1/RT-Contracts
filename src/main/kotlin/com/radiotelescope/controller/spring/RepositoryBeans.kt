package com.radiotelescope.controller.spring

import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

/**
 * Spring Beans class used to autowire all Spring Repositories
 *
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 */
@Component
@Configuration
class RepositoryBeans(
        val userRepo: IUserRepository,
        val userRoleRepo: IUserRoleRepository
)