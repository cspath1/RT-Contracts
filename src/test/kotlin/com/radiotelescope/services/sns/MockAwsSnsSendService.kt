package com.radiotelescope.services.sns

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.radiotelescope.service.sns.ErrorTag
import com.radiotelescope.service.sns.IAwsSnsSendService

class MockAwsSnsSendService(
        private val shouldSucceed: Boolean
) : IAwsSnsSendService {
    override fun execute(sendForm: SnsSendForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.MESSAGE, "Required field")

        return if (shouldSucceed) {
            null
        } else {
            errors
        }
    }
}