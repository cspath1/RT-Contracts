package com.radiotelescope.contracts.role

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation [UserRoleFactory] interface
 *
 * @param userRepo the [IUserRepository]
 * @param userRoleRepo the [IUserRoleRepository]
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository]
 */
class BaseUserRoleFactory(
        private val userRoleRepo: IUserRoleRepository,
        private val userRepo: IUserRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
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
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [UserRoleFactory.validate] method that will return a [Validate] command
     * object
     *
     * @param request the [Validate.Request] object
     * @return a [Validate] command object
     */
    override fun validate(request: Validate.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Validate(
                request = request,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [UserRoleFactory.retrieve] method that will return a [Retrieve] command
     * object
     *
     * @param id the UserRole id
     * @return a [Retrieve] command object
     */
    override fun retrieve(id: Long): Command<UserRoleInfo, Multimap<ErrorTag, String>> {
        return Retrieve(
                roleId = id,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [UserRoleFactory.requestRole] method that will return a [RequestRole] command
     * object
     *
     * @param request the [RequestRole.Request] object
     * @return a [RequestRole] command object
     */
    override fun requestRole(request: RequestRole.Request): Command<Long, Multimap<ErrorTag, String>> {
        return RequestRole(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }
}