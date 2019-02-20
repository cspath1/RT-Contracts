package com.radiotelescope.service.ses

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.ses.SendForm

/**
 * Interface used for sending emails via Amazon Web Services Simple Email Service.
 * Keeping this as an interface allows the application to use test implementations
 * during testing that will not actually contact AWS.
 */
interface IAwsSesSendService {
    /**
     * Send an email with the contents specified in the [SendForm]
     *
     * @param sendForm the [SendForm]
     * @return a [HashMultimap] of errors or null
     */
    fun execute(sendForm: SendForm): HashMultimap<ErrorTag, String>?
}