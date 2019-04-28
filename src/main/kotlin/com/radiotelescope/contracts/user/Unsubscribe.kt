package com.radiotelescope.contracts.user

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository

class Unsubscribe(
        private var id: Long,
        private var userRepo: IUserRepository,
        private val userNotificationType: IUserNotificationTypeRepository

) : Command<Long, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

        val builder = AmazonSNSClientBuilder.standard().withRegion("us-east-2").build()
        val topicARN = builder.createTopic("UserTopic" + id).topicArn

        //Todo: check userNotificationType and set protocol to email/sms correctly
        val subARN = builder.subscribe(topicARN,"email", userRepo.findById(id).get().email).subscriptionArn

        builder.unsubscribe(subARN)

        return SimpleResult(id, null)
    }
}