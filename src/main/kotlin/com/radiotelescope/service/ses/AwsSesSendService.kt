package com.radiotelescope.service.ses

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.ses.SendForm
import org.springframework.stereotype.Service

@Service
class AwsSesSendService(
        private val emailService: AmazonSimpleEmailService
) {
    fun execute(sendForm: SendForm): HashMultimap<ErrorTag, String>? {
        validateRequest(sendForm)?.let { return it } ?: let {
            val destination = Destination().withToAddresses(sendForm.toAddresses)
            val subject = Content().withData(sendForm.subject)
            val body = Body().withHtml(Content().withData(sendForm.htmlBody.replace("(\r\n|\n)", "<br />")))

            val message = Message().withBody(body).withSubject(subject)

            val email = SendEmailRequest()
                    .withSource(sendForm.fromAddress)
                    .withDestination(destination)
                    .withMessage(message)

            try {
                emailService.sendEmail(email)
            } catch (e: Exception) {
                val errors = HashMultimap.create<ErrorTag, String>()
                errors.put(ErrorTag.SEND_EMAIL, e.message)

                return errors
            }
        }

        return null
    }

    private fun validateRequest(sendForm: SendForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(sendForm) {
            if (toAddresses.isEmpty())
                errors.put(ErrorTag.TO_ADDRESSES, "Required field")
            if (fromAddress.isBlank())
                errors.put(ErrorTag.FROM_ADDRESS,"Required field")
            if (htmlBody.isBlank())
                errors.put(ErrorTag.EMAIL_BODY, "Required field")
            if (subject.isBlank())
                errors.put(ErrorTag.EMAIL_SUBJECT, "Required field")
        }

        return if (errors.isEmpty) null else errors
    }
}