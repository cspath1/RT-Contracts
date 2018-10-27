package com.radiotelescope.contracts.log

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.log.ILogRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation of the [LogFactory] interface
 *
 * @param logRepo the [ILogRepository] interface
 */
class BaseLogFactory(
        private val logRepo: ILogRepository
) : LogFactory {
    override fun list(pageable: Pageable): Command<Page<LogInfo>, Multimap<ErrorTag, String>> {
        return List(
                pageable = pageable,
                logRepo = logRepo
        )
    }
}