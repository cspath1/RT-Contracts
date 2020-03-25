package com.radiotelescope.services.ses

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.service.ses.ErrorTag
import com.radiotelescope.service.ses.IAwsSesSendService

class MockAwsSesSendService(
        private val shouldSucceed: Boolean
) : IAwsSesSendService {
    override fun execute(sendForm: SesSendForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.EMAIL_BODY, "Required field")

        return if (shouldSucceed) {
            null
        } else {
            errors
        }
    }
}