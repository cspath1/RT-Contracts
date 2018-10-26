package com.radiotelescope.contracts.log

import com.radiotelescope.repository.log.Log
import java.util.*

data class LogInfo(
        val id: Long,
        val userId: Long?,
        val affectedRecordId: Long?,
        val affectedTable: Log.AffectedTable,
        val action: String,
        val timestamp: Date,
        val isSuccess: Boolean
) {
    constructor(log: Log): this(
            id = log.id,
            userId = log.userId,
            affectedRecordId = log.affectedRecordId,
            affectedTable = log.affectedTable,
            action = log.action,
            timestamp = log.timestamp,
            isSuccess = log.isSuccess
    )
}