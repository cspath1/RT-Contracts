package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation of the [UserFactory] interface
 *
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 */
class BaseUserFactory(
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : UserFactory {
    /**
     * Override of the [UserFactory.register] method that will return a [Register] command object
     *
     * @param request the [Register.Request] object
     * @return a [Register] command object
     */
    override fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Register(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }

    /**
     * Override of the [UserFactory.authenticate] method that will return a [Authenticate] command object
     *
     * @param request the [Authenticate.Request] object
     * @return a [Authenticate] command object
     */
    override fun authenticate(request: Authenticate.Request): Command<UserInfo, Multimap<ErrorTag, String>> {
        return Authenticate(
                request = request,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UserFactory.retrieve] method that will return a [Retrieve] command object
     *
     * @param id the User id
     * @return a [Retrieve] command object
     */
    override fun retrieve(id: Long): Command<UserInfo, Multimap<ErrorTag, String>> {
        return Retrieve(
                id = id,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UserFactory.list] method that will return a [List] command object
     *
     * @param pageable the [Pageable] request
     * @return a [List] command object
     */
    override fun list(pageable: Pageable): Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
        return List(
                pageable = pageable,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UserFactory.update] method that will return a [Update] command object
     */
    override fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Update(
                request = request,
                userRepo = userRepo
        )
    }

    override fun delete(id: Long): Command<Long, Multimap<ErrorTag, String>> {
        return Delete(
                id = id,
                userRepo = userRepo
        )
    }
}