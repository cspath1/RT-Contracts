package com.radiotelescope.service.ses

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.ses.SendForm

class MockAwsSesSendService(
        private val shouldSucceed: Boolean
) : IAwsSesSendService {
    override fun execute(sendForm: SendForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.EMAIL_BODY, "Required field")

        return if (shouldSucceed) {
            null
        } else {
            errors
        }
    }
}