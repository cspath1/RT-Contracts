package com.radiotelescope.repository.sensorNetworkConfig

import javax.persistence.*

/**
 * Entity Class representing a Sensor Network Configuration for the
 * Sensor Network, which is related to the Radio Telescope.
 * This information will only be updated by the control room, thus,
 * has no contracts implementation.
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

     @Column(name = "timeout_data_retrieval")
     var timeoutDataRetrieval: Int,

     @Column(name = "timeout_initialization")
     var timeoutInitialization: Int,

     @Column(name = "elevation_temp_1_init")
     var elevationTemp1Init: Int,

     @Column(name = "elevation_temp_2_init")
     var elevationTemp2Init: Int,

     @Column(name = "azimuth_temp_1_init")
     var azimuthTemp1Init: Int,

     @Column(name = "azimuth_temp_2_init")
     var azimuthTemp2Init: Int,

     @Column(name = "azimuth_accelerometer_init")
     var azimuthAccelerometerInit: Int,

     @Column(name = "elevation_accelerometer_init")
     var elevationAccelerometerInit: Int,

     @Column(name = "counterbalance_accelerometer_init")
     var counterbalanceAccelerometerInit: Int,

     @Column(name = "azimuth_encoder_init")
     var azimuthEncoderInit: Int,

     @Column(name = "elevation_encoder_init")
     var elevationEncoderInit: Int
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    fun getId(): Long {
        return id
    }
}