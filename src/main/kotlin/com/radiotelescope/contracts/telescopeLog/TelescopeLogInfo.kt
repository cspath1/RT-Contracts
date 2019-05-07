package com.radiotelescope.contracts.telescopeLog

import com.radiotelescope.repository.telescopeLog.TelescopeLog
import java.util.*

/**
 * Data class representing a read-only model of the
 * [TelescopeLog] Entity
 *
 * @param id the Telescope Log's id
 * @param date the time the log was captured
 * @param logLevel the log level
 * @param thread the log thread
 * @param logger the log logger
 * @param message the log message
 */
data class TelescopeLogInfo(
        val id: Long,
        val date: Date,
        val logLevel: String,
        val thread: String,
        val logger: String,
        val message: String
) {
    /**
     * Secondary constructor that uses a [TelescopeLog]
     * to instantiate all fields
     *
     * @param telescopeLog the [TelescopeLog]
     */
    constructor(telescopeLog: TelescopeLog): this(
            id = telescopeLog.getId(),
            date = telescopeLog.getDate(),
            logLevel = telescopeLog.getLogLevel(),
            thread = telescopeLog.getThread(),
            logger = telescopeLog.getLogger(),
            message = telescopeLog.getMessage()
    )
}