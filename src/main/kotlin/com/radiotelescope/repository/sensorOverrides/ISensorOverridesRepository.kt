package com.radiotelescope.repository.sensorOverrides

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [SensorOverrides] Entity
 */
@Repository
interface ISensorOverridesRepository : CrudRepository<SensorOverrides, Long> {

    /**
     * Query for most recently added overrides for each sensor:
     * gives current override status (1, or 0) for each sensor
     * as a list of [SensorOverrides]
     */
    @Query(value = "SELECT * " +
            "FROM sensor_overrides " +
            "where sensor_name = ?1 ORDER BY id DESC LIMIT 1",
            nativeQuery = true
    )
    fun getMostRecentSensorOverridesByName(sensorName: String): SensorOverrides
}