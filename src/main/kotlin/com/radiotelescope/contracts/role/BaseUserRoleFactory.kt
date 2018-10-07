package com.radiotelescope.contracts.role

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation [UserRoleFactory] interface
 *
 * @param userRepo the [IUserRepository]
 * @param userRoleRepo the [IUserRoleRepository]
 */
class BaseUserRoleFactory(
        private val userRoleRepo: IUserRoleRepository,
        private val userRepo: IUserRepository
) : UserRoleFactory {
    /**
     * Override of the [UserRoleFactory.unapprovedList] method that will return a [UnapprovedList]
     * command object
     *
     * @param pageable the [Pageable] request
     * @return a [UnapprovedList] command object
     */
    override fun unapprovedList(pageable: Pageable): Command<Page<UserRoleInfo>, Multimap<ErrorTag, String>> {
        return UnapprovedList(
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }
}