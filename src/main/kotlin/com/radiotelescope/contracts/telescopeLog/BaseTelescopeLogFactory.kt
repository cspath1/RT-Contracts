package com.radiotelescope.contracts.telescopeLog

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation of the [TelescopeLogFactory] interface
 *
 * @param telescopeLogRepo the [ITelescopeLogRepository] interface
 */
class BaseTelescopeLogFactory(
        private val telescopeLogRepo: ITelescopeLogRepository
) : TelescopeLogFactory {
    /**
     * Override of the [TelescopeLogFactory.retrieve] method that will return a [Retrieve] command object
     *
     * @param id the Telescope Log id
     * @return a [Retrieve] command
     */
    override fun retrieve(id: Long): Command<TelescopeLogInfo, Multimap<ErrorTag, String>> {
        return Retrieve(
                telescopeLogId = id,
                telescopeLogRepo = telescopeLogRepo
        )
    }

    /**
     * Override of the [TelescopeLogFactory.list] method that will return a [List] command object
     *
     * @param pageable the [Pageable] interface
     * @return a [List] command
     */
    override fun list(pageable: Pageable): Command<Page<TelescopeLogInfo>, Multimap<ErrorTag, String>> {
        return List(
                pageable = pageable,
                telescopeLogRepo = telescopeLogRepo
        )
    }
}