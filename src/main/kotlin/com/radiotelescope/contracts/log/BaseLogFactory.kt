package com.radiotelescope.contracts.log

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation of the [LogFactory] interface
 *
 * @param logRepo the [ILogRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class BaseLogFactory(
        private val logRepo: ILogRepository,
        private val userRepo: IUserRepository
) : LogFactory {
    /**
     * Override of the [LogFactory.list] method that will return a [List]
     * command object
     *
     * @param pageable the [Pageable] interface
     * @return a [List] command object
     */
    override fun list(pageable: Pageable): Command<Page<LogInfo>, Multimap<ErrorTag, String>> {
        return List(
                pageable = pageable,
                logRepo = logRepo,
                userRepo = userRepo
        )
    }
}