package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository

/**
 * Base concrete implementation of the [SensorStatusFactory] interface
 *
 * @param sensorStatusRepo the [ISensorStatusRepository] interface
 */
class BaseSensorStatusFactory(
        private val sensorStatusRepo: ISensorStatusRepository
) : SensorStatusFactory {

    /**
     * Override of the [SensorStatusFactory.create] method that will return a [Create] command
     *
     * @param request the [Create.Request] object
     * @param id the uuid used to verify control room access
     * @return a [Create] command object
     */
    override fun create(request: Create.Request, id: String): Command<Long, Multimap<ErrorTag, String>> {
        return Create(
                request = request,
                sensorStatusRepo = sensorStatusRepo,
                id = id
        )
    }
}