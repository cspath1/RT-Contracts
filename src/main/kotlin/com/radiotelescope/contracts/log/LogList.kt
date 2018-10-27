package com.radiotelescope.contracts.log

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface method user for Log LogList retrieval
 *
 * @param pageable the [Pageable] interface
 * @param logRepo the [ILogRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class LogList(
        private val pageable: Pageable,
        private val logRepo: ILogRepository,
        private val userRepo: IUserRepository
) : Command<Page<LogInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that calls the [ILogRepository.findAll]
     * method and passes in the pageable parameter. It then adapts the [Page] of Logs
     * into a [Page] of [LogInfo] objects and returns it in the [SimpleResult]
     */
    override fun execute(): SimpleResult<Page<LogInfo>, Multimap<ErrorTag, String>> {
        val logPage = logRepo.findAll(pageable)

        val infoList = arrayListOf<LogInfo>()
        logPage.forEach {
            if (it.userId != null) {
                val theUser = userRepo.findById(it.userId!!).get()
                infoList.add(LogInfo(log = it, user = theUser))
            } else {
                infoList.add(LogInfo(it))
            }
        }

        val infoPage = PageImpl(infoList, logPage.pageable, logPage.totalElements)
        return SimpleResult(infoPage, null)
    }
}