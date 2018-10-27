package com.radiotelescope.contracts.log

import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.User
import java.util.*

data class LogInfo(
        val id: Long,
        val userId: Long?,
        val userFirstName: String?,
        val userLastName: String?,
        val affectedRecordId: Long?,
        val affectedTable: Log.AffectedTable,
        val action: String,
        val timestamp: Date,
        val isSuccess: Boolean
) {
    constructor(log: Log): this(
            id = log.id,
            userId = null,
            userFirstName = null,
            userLastName = null,
            affectedRecordId = log.affectedRecordId,
            affectedTable = log.affectedTable,
            action = log.action,
            timestamp = log.timestamp,
            isSuccess = log.isSuccess
    )

    constructor(log: Log, user: User): this(
            id = log.id,
            userId = log.userId,
            userFirstName = user.firstName,
            userLastName = user.lastName,
            affectedRecordId = log.affectedRecordId,
            affectedTable = log.affectedTable,
            action = log.action,
            timestamp = log.timestamp,
            isSuccess = log.isSuccess
    )
}