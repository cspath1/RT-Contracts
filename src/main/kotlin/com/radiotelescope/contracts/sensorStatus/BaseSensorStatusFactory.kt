package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import com.radiotelescope.repository.sensorStatus.SensorStatus

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
     * @param uuid the uuid used to verify control room access
     * @return a [Create] command object
     */
    override fun create(request: Create.Request, uuid: String): Command<Long, Multimap<ErrorTag, String>> {
        return Create(
                request = request,
                sensorStatusRepo = sensorStatusRepo,
                uuid = uuid
        )
    }

    /**
     * Override of the [SensorStatusFactory.retrieve] method that will return a [Retrieve]
     * command object
     *
     * @param id the [SensorStatus] id
     * @return a [Retrieve] command object
     */
    override fun retrieve(id: Long): Command<SensorStatus, Multimap<ErrorTag, String>> {
        return Retrieve(
                sensorStatusId = id,
                sensorStatusRepo = sensorStatusRepo
        )
    }
}