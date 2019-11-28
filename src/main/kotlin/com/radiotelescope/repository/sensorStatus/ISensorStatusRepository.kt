package com.radiotelescope.repository.sensorStatus

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [SensorStatus] Entity
 */
@Repository
interface ISensorStatusRepository: PagingAndSortingRepository<SensorStatus, Long>