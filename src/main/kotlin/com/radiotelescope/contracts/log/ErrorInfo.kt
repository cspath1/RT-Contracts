package com.radiotelescope.contracts.log

import com.radiotelescope.repository.error.Error
import com.radiotelescope.repository.log.Log

/**
 * Data class representing a read-only model of the
 * [Error] Entity
 *
 * @param logId the Error's [Log] id
 * @param field the Error's field/map key value
 * @param message the Error's message
 */
data class ErrorInfo(
        val logId: Long,
        val field: String,
        val message: String
) {
    /**
     * Secondary constructor that takes an error object
     * and a log id to set all fields
     *
     * @param error the [Error]
     * @param logId the [Log] id
     */
    constructor(error: Error, logId: Long): this(
            logId = logId,
            field = error.field,
            message = error.message
    )
}