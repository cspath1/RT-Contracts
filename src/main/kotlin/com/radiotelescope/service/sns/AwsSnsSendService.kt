package com.radiotelescope.service.sns

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.sns.SendForm
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
     * Execute method that takes a [SendForm] and, given there are no errors with the request,
     * will send a notification using the AWS Simple Notification Service
     */
    override fun execute(sendForm: SendForm): HashMultimap<ErrorTag, String>? {
        validateRequest(sendForm)?.let { return it } ?: let {
            val smsAttributes = HashMap<String, MessageAttributeValue>()

            val request: PublishRequest = PublishRequest()
                    .withMessage(sendForm.message)
                    .withPhoneNumber("+1" + sendForm.toNumber)
                    .withMessageAttributes(smsAttributes)

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
     * @param sendForm the [SendForm]
     * @return a [HashMultimap] if there are errors null otherwise
     */
    private fun validateRequest(sendForm: SendForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(sendForm) {
            if (toNumber.isEmpty())
                errors.put(ErrorTag.TO_NUMBER, "Required Field")
            if (topic.isEmpty())
                errors.put(ErrorTag.TOPIC, "Required Field")
            if (message.isEmpty())
                errors.put(ErrorTag.MESSAGE, "Required Field")
        }

        return if (errors.isEmpty) null else errors
    }
}