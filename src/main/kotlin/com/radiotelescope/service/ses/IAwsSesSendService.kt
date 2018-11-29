package com.radiotelescope.service.ses

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.ses.SendForm

interface IAwsSesSendService {
    fun execute(sendForm: SendForm): HashMultimap<ErrorTag, String>?
}