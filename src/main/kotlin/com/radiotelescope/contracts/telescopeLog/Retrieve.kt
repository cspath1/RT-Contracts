package com.radiotelescope.contracts.telescopeLog

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import com.radiotelescope.repository.telescopeLog.TelescopeLog

/**
 * Concrete implementation of the [Command] interface method used to retrieve [TelescopeLog]
 * information
 *
 * @param telescopeLogId the [TelescopeLog] id
 * @param telescopeLogRepo the [ITelescopeLogRepository] interface
 */
class Retrieve(
        private val telescopeLogId: Long,
        private val telescopeLogRepo: ITelescopeLogRepository
) : Command<TelescopeLogInfo, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. It checks to see if the
     * supplied id refers to an existing [TelescopeLog] Entity, and if so,
     * will retrieve it and adapt it to a [TelescopeLogInfo] data class.
     * It will then return this information in a [SimpleResult].
     *
     * If the telescope log does not exist, it will return an error in the
     * [SimpleResult]
     */
    override fun execute(): SimpleResult<TelescopeLogInfo, Multimap<ErrorTag, String>> {
        if (!telescopeLogRepo.existsById(telescopeLogId)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Telescope Log Id #$telescopeLogId not found")
            return SimpleResult(null, errors)
        }

        val theTelescopeLog = telescopeLogRepo.findById(telescopeLogId).get()
        val theInfo = TelescopeLogInfo(theTelescopeLog)

        return SimpleResult(theInfo, null)
    }
}