package com.radiotelescope.contracts.log

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.log.ILogRepository

class RetrieveErrors(
        private val logId: Long,
        private val logRepo: ILogRepository
) : Command<List<ErrorInfo>, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<List<ErrorInfo>, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let { _ ->
            val theLog = logRepo.findById(logId).get()

            val infoList = arrayListOf<ErrorInfo>()
            theLog.errors.forEach {
                infoList.add(ErrorInfo(it, theLog.id))
            }

            return SimpleResult(infoList, null)
        }
    }

    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!logRepo.existsById(logId)) {
            errors.put(ErrorTag.ID, "Log #$logId could not be found")
            return errors
        }

        val theLog = logRepo.findById(logId).get()
        if (theLog.isSuccess)
            errors.put(ErrorTag.SUCCESS, "Log #$logId was a success and has no errors")

        return if (errors.isEmpty) null else errors
    }
}