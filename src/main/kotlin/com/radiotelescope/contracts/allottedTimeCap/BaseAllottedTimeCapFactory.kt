package com.radiotelescope.contracts.allottedTimeCap

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.allottedTimeCap.AllottedTimeCap
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Base concrete implementation of the [AllottedTimeCapFactory] interface
 *
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
class BaseAllottedTimeCapFactory(
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
) : AllottedTimeCapFactory {

    /**
     * Override of the [AllottedTimeCapFactory.update] method that will return an [Update] command object
     *
     * @param request the [Update.Request] object
     * @return a [Update] command object
     */
    override fun update(request: Update.Request): Command<AllottedTimeCap, Multimap<ErrorTag, String>> {
        return Update(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }
}