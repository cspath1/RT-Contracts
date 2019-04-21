package com.radiotelescope.contracts.user

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository

class Subscribe (
        private var id: Long,
        private var userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val userNotificationType: IUserNotificationTypeRepository
): Command<Long, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {

        val builder = AmazonSNSClientBuilder.standard().withRegion("us-east-2").build()
        val topicARN = builder.createTopic("userTopic" + id).topicArn


        //TODO: check if userNotificationType is email or phone and subscribe/unsubscribe correctly
        //user is not currently subscribed to the topic
        if (builder.listSubscriptions(topicARN).subscriptions.size == 0)
            builder.subscribe(topicARN,"email", userRepo.findById(id).get().email)
        else
            builder.unsubscribe(builder.subscribe(topicARN,"email",userRepo.findById(id).get().email).subscriptionArn)

        return SimpleResult(id, null)
    }

}