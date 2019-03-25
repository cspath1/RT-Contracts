package com.radiotelescope.contracts.allottedTimeCap

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.allottedTimeCap.AllottedTimeCap

/**
 * Abstract factory interface with methods for all [AllottedTimeCap] CRUD operations.
 */
interface AllottedTimeCapFactory {

    /**
     * Abstract command used to update the allotted time cap
     *
     * @param request the [Update.Request] request
     * @return a [Command] object
     */
    fun update(request: Update.Request): Command<AllottedTimeCap, Multimap<ErrorTag, String>>
}