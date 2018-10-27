package com.radiotelescope.contracts.log

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.toLogInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class List(
        private val pageable: Pageable,
        private val logRepo: ILogRepository
) : Command<Page<LogInfo>, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Page<LogInfo>, Multimap<ErrorTag, String>> {
        val logPage = logRepo.findAll(pageable)
        return SimpleResult(logPage.toLogInfoPage(), null)
    }
}