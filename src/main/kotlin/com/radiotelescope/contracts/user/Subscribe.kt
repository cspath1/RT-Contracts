package com.radiotelescope.contracts.user

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.UnsubscribeRequest
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
        val topicARN = builder.createTopic("UserTopic" + id).topicArn


        //TODO: check if userNotificationType is email or phone and subscribe/unsubscribe correctly
        //user is not currently subscribed to the topic
        //if (builder.listSubscriptions(builder.listSubscriptionsByTopic(topicARN).nextToken).subscriptions.size < 2)
            builder.subscribe(topicARN,"email", userRepo.findById(id).get().email)
        //temporary implementation for unsubscribing a user from a topic by deleting and recreating topic
        //else{
            //builder.unsubscribe(builder.listSubscriptionsByTopic(topicARN).subscriptions[0].subscriptionArn)
//            builder.deleteTopic(topicARN)
//            builder.createTopic("UserTopic" + id)
//        }

            //builder.unsubscribe(builder.listSubscriptionsByTopic(topicARN).subscriptions[0].subscriptionArn)
       // builder.unsubscribe("arn:aws:sns:us-east-2:317377631261:UserTopic1:4f69a002-95f8-4d31-87b3-9c815fd66d99")


        return SimpleResult(id, null)
    }

}