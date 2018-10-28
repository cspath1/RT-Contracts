package com.radiotelescope.contracts.log

import com.radiotelescope.repository.error.Error

data class ErrorInfo(
        val logId: Long,
        val field: String,
        val message: String
) {
    constructor(error: Error, logId: Long): this(
            logId = logId,
            field = error.field,
            message = error.message
    )
}