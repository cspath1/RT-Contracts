package com.radiotelescope.service.sns

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.sns.SendForm

/**
 * Interface used for sending notifications via Amazon Web Services Simple Notification Service.
 * Keeping this as an interface allows the application to use test implementations
 * during testing that will not actually contact AWS.
 */
interface IAwsSnsSendService {
    /**
     * Send an email with the contents specified in the [SendForm]
     *
     * @param sendForm the [SendForm]
     * @return a [HashMultimap] of errors or null
     */
    fun execute(sendForm: SendForm): HashMultimap<ErrorTag, String>?
}