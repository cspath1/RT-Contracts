package com.radiotelescope.repository.thresholds

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [Thresholds] Entity
 */
@Repository
interface IThresholdsRepository: CrudRepository<Thresholds, Long> {

    @Query(value = "SELECT * " +
            "FROM thresholds " +
            "where sensor_name = ?1 ORDER BY id DESC LIMIT 1",
            nativeQuery = true
    )
    fun getMostRecentThresholdByName(sensorName: String): Thresholds
}