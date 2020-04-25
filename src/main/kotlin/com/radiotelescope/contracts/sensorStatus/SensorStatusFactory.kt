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
     * @param uuid the uuid used to verify control room access
     * @param profile the user profile
     * @return a [Command] object
     */
    fun create(request: Create.Request, uuid: String, profile: String): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a [SensorStatus] object by id
     *
     * @param id the [SensorStatus] object's id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<SensorStatus, Multimap<ErrorTag,String>>

    /**
     * Abstract command used to retrieve a [SensorStatus] object by
     * most recently added
     *
     * @return a [Command] object
     */
    fun getMostRecent(): Command<SensorStatus, Multimap<ErrorTag,String>>

}