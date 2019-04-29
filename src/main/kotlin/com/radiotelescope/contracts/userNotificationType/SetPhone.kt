package com.radiotelescope.contracts.userNotificationType

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository
import com.radiotelescope.repository.userNotificationType.UserNotificationType

class SetPhone(
        private val userNotificationTypeRepo: IUserNotificationTypeRepository,
        private val id: Long
):Command<Long, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

        val userNotificationType = userNotificationTypeRepo.findById(id).get()
        userNotificationType.type = UserNotificationType.NotificationType.PHONE
        userNotificationTypeRepo.save(userNotificationType)

        return SimpleResult(userNotificationType.id, null)
    }
}