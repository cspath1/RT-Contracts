package com.radiotelescope.contracts.user

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository

class Subscribe (
        private var id: Long,
        private var userRepo: IUserRepository,
        private val userNotificationType: IUserNotificationTypeRepository
): Command<Long, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

        val builder = AmazonSNSClientBuilder.standard().withRegion("us-east-2").build()
        val topicARN = builder.createTopic("UserTopic" + id).topicArn


        //TODO: check if userNotificationType is email or phone and subscribe/unsubscribe correctly
        //user is not currently subscribed to the topic
        //if (builder.listSubscriptions(builder.listSubscriptionsByTopic(topicARN).nextToken).subscriptions.size < 2)
            builder.subscribe(topicARN,"email", userRepo.findById(id).get().email)

        return SimpleResult(id, null)
    }

}