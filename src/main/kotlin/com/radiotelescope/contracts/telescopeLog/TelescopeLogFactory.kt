package com.radiotelescope.contracts.telescopeLog

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.telescopeLog.TelescopeLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract factory interface with methods for all [TelescopeLog] CRUD operations
 */
interface TelescopeLogFactory {
    /**
     * Abstract command used to retrieve a specific log for a telescope.
     *
     * @param id the Telescope Log id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<TelescopeLogInfo, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a list of telescope logs
     *
     * @param pageable the [Pageable] interface
     * @return a [Command] object
     */
    fun list(pageable: Pageable): Command<Page<TelescopeLogInfo>, Multimap<ErrorTag, String>>
}