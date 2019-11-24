package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

/**
 * Wrapper that takes a [SensorStatusFactory] and is responsible for all
 * user role validations for the Feedback Entity
 *
 * @param factory the [SensorStatusFactory] interface
 */
class UserSensorStatusWrapper(
    private val factory: SensorStatusFactory
) {
    /**
     * Wrapper method for the [SensorStatusFactory.create] method.
     *
     * @param request the [Create.Request] object
     */
    fun create(request: Create.Request, id: String): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request, id)
    }
}