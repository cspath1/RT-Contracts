package com.radiotelescope.contracts.log

import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.User
import java.util.*

/**
 * Data class representing a read-only model of
 * the [Log] Entity
 *
 * @param id the Log's id
 * @param user the Log's user
 * @param userFirstName the Log's user's first name
 * @param userLastName the Log's user's last name
 * @param affectedRecordId the Log's affected record id
 * @param affectedTable the Log's affected table
 * @param action the Log's action
 * @param timestamp the Log's timestamp
 * @param isSuccess whether the action was a success or not
 */
data class LogInfo(
        val id: Long,
        val userFirstName: String?,
        val userLastName: String?,
        val affectedRecordId: Long?,
        val affectedTable: String?,
        val action: String,
        val timestamp: Date,
        val isSuccess: Boolean,
        val status: Int
) {
    /**
     * Secondary constructor used when there was no logged
     * in user making the action
     *
     * @param log the [Log]
     */
    constructor(log: Log): this(
            id = log.id,
            userFirstName = null,
            userLastName = null,
            affectedRecordId = log.affectedRecordId,
            affectedTable = log.affectedTable?.label,
            action = log.action,
            timestamp = log.timestamp,
            isSuccess = log.isSuccess,
            status = log.status
    )

    /**
     * Secondary constructor used when there was a logged
     * in user making the action
     *
     * @param log the [Log]
     * @param user the [User]
     */
    constructor(log: Log, user: User): this(
            id = log.id,
            userFirstName = user.firstName,
            userLastName = user.lastName,
            affectedRecordId = log.affectedRecordId,
            affectedTable = log.affectedTable?.label,
            action = log.action,
            timestamp = log.timestamp,
            isSuccess = log.isSuccess,
            status = log.status
    )
}