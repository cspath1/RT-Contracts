package com.radiotelescope.contracts.log

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.log.ILogRepository

/**
 * Override of the [Command] interface used for retrieving Log errors
 *
 * @param logId the Log id
 * @param logRepo the [ILogRepository] interface
 */
class RetrieveErrors(
        private val logId: Long,
        private val logRepo: ILogRepository
) : Command<List<ErrorInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that returns all errors for a given log
     */
    override fun execute(): SimpleResult<List<ErrorInfo>, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theLog = logRepo.findById(logId).get()

            val infoList = arrayListOf<ErrorInfo>()
            theLog.errors.forEach { error ->
                infoList.add(ErrorInfo(error, theLog.id))
            }

            return SimpleResult(infoList, null)
        }
    }

    /**
     * Method responsible for constraint checking/validation. It ensures
     * a record with the given log id exists and was indeed a failure,
     * since logs for successful actions will not have an errors
     */
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