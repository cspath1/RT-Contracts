package com.radiotelescope.services.sns

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.radiotelescope.controller.model.sns.SnsSubscribeForm
import com.radiotelescope.service.sns.ErrorTag
import com.radiotelescope.service.sns.IAwsSnsService

class MockAwsSnsService(
        private val shouldSucceed: Boolean
) : IAwsSnsService {
    override fun send(sendForm: SnsSendForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.MESSAGE, "Required field")

        return if (shouldSucceed) {
            null
        } else {
            errors
        }
    }

    override fun subscribe(subscribeForm: SnsSubscribeForm): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.TOPIC, "Required field")

        return if (shouldSucceed) {
            null
        } else {
            errors
        }
    }
}