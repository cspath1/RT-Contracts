package com.radiotelescope.repository.sensorNetworkConfig

import javax.persistence.*

/**
 * Entity Class representing a Sensor Network Configuration for the
 * Sensor Network, which is related to the Radio Telescope.
 * This information will only be updated by the control room, thus,
 * has no contracts implementation thus far.
 *
 * It is possible, if the desire to modify these settings from the
 * mobile or web applications arises, we may need to implement
 * contracts logic and an API call.
 *
 * This Entity correlates to the sensor_network_config SQL table.
 */

@Entity
@Table(name = "sensor_network_config")
data class SensorNetworkConfig(

     // We do not want this to be a foreign key because, in our design,
     // the SensorNetworkConfig does NOT have a whole Radio Telescope,
     // only its ID.
     @Column(name = "telescope_id")
     var telescopeId: Int,

     @Column(name = "sensor_initialization")
     var sensorInitialization: Int,

     @Column(name = "timeout_data_retrieval")
     var timeoutDataRetrieval: Int,

     @Column(name = "timeout_initialization")
     var timeoutInitialization: Int
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    fun getId(): Long {
        return id
    }
}