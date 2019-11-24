package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.sensorStatus.SensorStatus

/**
 * Abstract factory interface with methods for all [SensorStatus] operations
 */
interface SensorStatusFactory {

    /**
     * Abstract command used to create a new [SensorStatus] object
     *
     * @param request the [Create.Request] request
     * @param id the uuid used to verify control room access
     * @return a [Command] object
     */
    fun create(request: Create.Request, id: String): Command<Long, Multimap<ErrorTag, String>>
}