package com.radiotelescope.service.sns

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.MessageAttributeValue
import com.amazonaws.services.sns.model.PublishRequest
import org.springframework.stereotype.Service

/**
 * Service used to send notifications using the AWS Simple Notification Service.
 * Can send to individual phone numbers or topics
 *
 * @param notificationService the [AmazonSNS] service.
 */
@Service
class AwsSnsSendService (
    private val notificationService: AmazonSNS
) : IAwsSnsSendService {
    /**
     * Execute method that takes a [SnsSendForm] and, given there are no errors with the request,
     * will send a notification using the AWS Simple Notification Service
     */
    override fun execute(sendForm: SnsSendForm): HashMultimap<ErrorTag, String>? {
        validateRequest(sendForm)?.let { return it } ?: let {
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
     * Private method used to validate the request to send the notification
     *
     * @param sendForm the [SnsSendForm]
     * @return a [HashMultimap] if there are errors null otherwise
     */
    private fun validateRequest(sendForm: SnsSendForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(sendForm) {
            if (toNumber.isNullOrBlank() && topic.isNullOrBlank())
                errors.put(ErrorTag.TYPE, "Send Type must be either to Phone Number or to Topic")
            if (message.isEmpty())
                errors.put(ErrorTag.MESSAGE, "Required Field")
        }

        return if (errors.isEmpty) null else errors
    }
}