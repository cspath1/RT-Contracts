package com.radiotelescope.service.heartbeat

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult

/**
 * Interface used for handling a heartbeat received from a local server
 */
interface  IHandleHeartbeatService {
    /**
     * Execute method that will handle receiving communication from a local server.
     * This service is used by the production server only, and local servers will be
     * in charge of contacting the production server
     */
    fun execute(radioTelescopeId: Long): SimpleResult<Long, Multimap<ErrorTag, String>>
}