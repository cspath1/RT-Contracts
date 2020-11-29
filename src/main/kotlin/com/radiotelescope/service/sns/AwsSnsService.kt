package com.radiotelescope.service.sns

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.MessageAttributeValue
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.SubscribeRequest
import com.radiotelescope.controller.model.sns.SnsSubscribeForm
import org.springframework.stereotype.Service

/**
 * Service used to send notifications using the AWS Simple Notification Service.
 * Can send to individual phone numbers or topics
 *
 * @param notificationService the [AmazonSNS] service.
 */
@Service
class AwsSnsService (
    private val notificationService: AmazonSNS
) : IAwsSnsService {
    /**
     * Execute method that takes a [SnsSendForm] and, given there are no errors with the request,
     * will send a notification using the AWS Simple Notification Service
     */
    override fun send(sendForm: SnsSendForm): HashMultimap<ErrorTag, String>? {
        validateSendRequest(sendForm)?.let { return it } ?: let {
            val smsAttributes = HashMap<String, MessageAttributeValue>()

            // if topic is blank, send an SMS
            // if phone number is plank, post to topic
            var request: PublishRequest = PublishRequest()

            if (sendForm.topic.isNullOrBlank()) {
                request = PublishRequest()
                        .withMessage(sendForm.message)
                        .withPhoneNumber("+1" + sendForm.toNumber)
                        .withMessageAttributes(smsAttributes)
            }

            if (sendForm.toNumber.isNullOrBlank()) {
                request = PublishRequest(sendForm.topic, sendForm.message)
                        .withMessageAttributes(smsAttributes)
            }

            try {
                notificationService.publish(request)
            } catch (e: Exception) {
                val errors = HashMultimap.create<ErrorTag, String>()
                print("Error sending notification: ${e.message}")
                errors.put(ErrorTag.SEND_MESSAGE, e.message)

                return errors
            }
        }

        return null
    }

    /**
     * Execute method that takes a [SnsSubscribeForm] and, given there are no errors with the request,
     * will subscribe a user to a topic using the AWS Simple Notification Service
     */
    override fun subscribe(subscribeForm: SnsSubscribeForm): HashMultimap<ErrorTag, String>? {
        validateSubscribeRequest(subscribeForm)?.let { return it } ?: let {
            val subscribeRequest = SubscribeRequest()
                    .withTopicArn(subscribeForm.topic)
                    .withProtocol(subscribeForm.protocol)
                    .withEndpoint(subscribeForm.endpoint)

            try {
                notificationService.subscribe(subscribeRequest)
            } catch (e: Exception) {
                val errors = HashMultimap.create<ErrorTag, String>()
                print("Error sending notification: ${e.message}")
                errors.put(ErrorTag.SEND_MESSAGE, e.message)

                return errors
            }

            print("SubscribeRequest: " + notificationService.getCachedResponseMetadata(subscribeRequest))
        }

        return null
    }

    /**
     * Private method used to validate the request to send the notification
     *
     * @param sendForm the [SnsSendForm]
     * @return a [HashMultimap] if there are errors null otherwise
     */
    private fun validateSendRequest(sendForm: SnsSendForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(sendForm) {
            if (toNumber.isNullOrBlank() && topic.isNullOrBlank())
                errors.put(ErrorTag.TYPE, "Send Type must be either to Phone Number or to Topic")
            if (message.isEmpty())
                errors.put(ErrorTag.MESSAGE, "Required Field")
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Private method used to validate the request to subscribe an endpoint
     *
     * @param subscribeForm the [SnsSubscribeForm]
     * @return a [HashMultimap] if there are errors null otherwise
     */
    private fun validateSubscribeRequest(subscribeForm: SnsSubscribeForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(subscribeForm) {
            if (topic.isEmpty())
                errors.put(ErrorTag.TOPIC, "Required Field")
            if (endpoint.isEmpty())
                errors.put(ErrorTag.TO_NUMBER, "Required Field")
            if (protocol != "email" && protocol != "sms")
                errors.put(ErrorTag.PROTOCOL, "Protocol Must Be 'email' or 'sms'")
        }

        return if (errors.isEmpty) null else errors
    }
}