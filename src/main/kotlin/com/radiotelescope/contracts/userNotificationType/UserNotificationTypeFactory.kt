package com.radiotelescope.contracts.userNotificationType

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

interface UserNotificationTypeFactory {
    /**
     * Abstract command used to set a user's notification type to email
     *
     * @param request the [SetEmail.Request] request
     * @return a [Command] object
     */
    fun setEmail(request: SetEmail.Request): Command<Long, Multimap<ErrorTag, String>>

    fun setPhone(id: Long): Command<Long, Multimap<ErrorTag, String>>


}