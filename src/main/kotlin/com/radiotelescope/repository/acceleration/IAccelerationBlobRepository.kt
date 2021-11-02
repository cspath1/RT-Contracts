package com.radiotelescope.repository.acceleration

import com.radiotelescope.repository.location.Location
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [AzimuthAccelerationBlob] Entity
 */
@Repository
interface IAzimuthAccelerationBlobRepository : CrudRepository<AzimuthAccelerationBlob, Long>

/**
 * Spring Repository for the [ElevationAccelerationBlob] Entity
 */
@Repository
interface IElevationAccelerationBlobRepository : CrudRepository<ElevationAccelerationBlob, Long>

/**
 * Spring Repository for the [CounterbalanceAccelerationBlob] Entity
 */
@Repository
interface ICounterbalanceAccelerationBlobRepository : CrudRepository<CounterbalanceAccelerationBlob, Long>