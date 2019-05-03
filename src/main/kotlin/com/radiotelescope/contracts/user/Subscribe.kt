package com.radiotelescope.contracts.user

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository
import com.radiotelescope.repository.userNotificationType.UserNotificationType

class Subscribe (
        private var id: Long,
        private var userRepo: IUserRepository,
        private val userNotificationType: IUserNotificationTypeRepository
): Command<Long, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

        val builder = AmazonSNSClientBuilder.standard().build()
        val topicARN = builder.createTopic("UserTopic" + id).topicArn


        //user is to be notified by email
        if (userNotificationType.findById(id).get().type == UserNotificationType.NotificationType.EMAIL){
            builder.subscribe(topicARN,"email", userRepo.findById(id).get().email)
        }
        //user is to be notified by sms
        else if(userNotificationType.findById(id).get().type == UserNotificationType.NotificationType.PHONE){
            builder.subscribe(topicARN, "sms", userRepo.findById(id).get().phoneNumber)
        }

        builder.shutdown()
        return SimpleResult(id, null)
    }

}