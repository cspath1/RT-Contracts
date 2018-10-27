package com.radiotelescope.contracts.log

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.toLogInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface method user for Log List retrieval
 *
 * @param pageable the [Pageable] interface
 * @param logRepo the [ILogRepository] interface
 */
class List(
        private val pageable: Pageable,
        private val logRepo: ILogRepository
) : Command<Page<LogInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that calls the [ILogRepository.findAll]
     * method and passes in the pageable parameter. It then adapts the [Page] of Logs
     * into a [Page] of [LogInfo] objects and returns it in the [SimpleResult]
     */
    override fun execute(): SimpleResult<Page<LogInfo>, Multimap<ErrorTag, String>> {
        val logPage = logRepo.findAll(pageable)
        return SimpleResult(logPage.toLogInfoPage(), null)
    }
}