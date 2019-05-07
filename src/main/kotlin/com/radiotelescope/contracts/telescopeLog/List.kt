package com.radiotelescope.contracts.telescopeLog

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import com.radiotelescope.toTelescopeInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Concrete implementation of the [Command] interface used to retrieve a list
 * of telescope logs
 *
 * @param pageable the [Pageable] interface
 * @param telescopeLogRepo the [ITelescopeLogRepository] interface
 */
class List(
        private val pageable: Pageable,
        private val telescopeLogRepo: ITelescopeLogRepository
) : Command<Page<TelescopeLogInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that calls the [ITelescopeLogRepository.findAll]
     * method and passes in the pageable parameter. It then adapts the Page of Telescope Logs
     * into a Page of [TelescopeLogInfo] objects
     */
    override fun execute(): SimpleResult<Page<TelescopeLogInfo>, Multimap<ErrorTag, String>> {
        // Execute the command
        val logPage = telescopeLogRepo.findAll(pageable)

        // Adapt into a page of info objects
        val infoPage = logPage.toTelescopeInfoPage()

        return SimpleResult(infoPage, null)
    }
}