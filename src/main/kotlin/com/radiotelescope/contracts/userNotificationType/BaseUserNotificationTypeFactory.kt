package com.radiotelescope.contracts.userNotificationType

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository

class BaseUserNotificationTypeFactory
(
        private val userRepo: IUserRepository,
        private val userNotificationTypeRepo: IUserNotificationTypeRepository
        ): UserNotificationTypeFactory{

    override fun setEmail(request : SetEmail.Request): Command<Long, Multimap<ErrorTag, String>> {
        return SetEmail(
                userNotificationTypeRepo = userNotificationTypeRepo,
                request = request
        )
    }
}