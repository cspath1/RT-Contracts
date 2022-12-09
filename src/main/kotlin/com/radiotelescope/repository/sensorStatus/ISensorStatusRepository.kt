package com.radiotelescope.repository.sensorStatus

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [SensorStatus] Entity
 */
@Repository
interface ISensorStatusRepository: PagingAndSortingRepository<SensorStatus, Long> {

    /**
     * Spring Repository method that will return the most recent
     * [SensorStatus] record
     *
     * @return a [SensorStatus] object
     */
    @Query(value = "SELECT * FROM sensor_status ORDER BY id DESC LIMIT 1",
            nativeQuery = true
    )
    fun getMostRecentSensorStatus(): SensorStatus
}