package com.radiotelescope.contracts.feedback

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

class UserFeedbackWrapper(
        private val factory: FeedbackFactory
) {
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request)
    }
}