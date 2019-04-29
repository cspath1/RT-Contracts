package com.radiotelescope.contracts.userNotificationType

import com.radiotelescope.repository.userNotificationType.UserNotificationType

data class UserNotificationTypeInfo(
        val id: Long,
        val type: UserNotificationType.NotificationType,
        val userId: Long
) {
    constructor(userNotificationType: UserNotificationType): this(
            id = userNotificationType.id,
            type = userNotificationType.type,
            userId = userNotificationType.userId
    )
}
