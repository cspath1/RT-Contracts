package com.radiotelescope.service.sns

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.model.sns.SnsSendForm

/**
 * Interface used for sending notifications via Amazon Web Services Simple Notification Service.
 * Keeping this as an interface allows the application to use test implementations
 * during testing that will not actually contact AWS.
 */
interface IAwsSnsSendService {
    /**
     * Send an email with the contents specified in the [SnsSendForm]
     *
     * @param sendForm the [SnsSendForm]
     * @return a [HashMultimap] of errors or null
     */
    fun execute(sendForm: SnsSendForm): HashMultimap<ErrorTag, String>?
}