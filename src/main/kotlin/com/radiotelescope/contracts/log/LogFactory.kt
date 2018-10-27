package com.radiotelescope.contracts.log

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.log.Log
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract factory interface with methods for all [Log] Command objects
 */
interface LogFactory {
    /**
     * Abstract command used to retrieve a list of logs
     *
     * @param pageable the [Pageable] interface
     * @return a [Command] object
     */
    fun list(pageable: Pageable): Command<Page<LogInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a list of error messages
     * for a given log
     *
     * @param logId the Log id
     * @return a [Command] object
     */
    fun retrieveErrors(logId: Long): Command<List<ErrorInfo>, Multimap<ErrorTag, String>>
}