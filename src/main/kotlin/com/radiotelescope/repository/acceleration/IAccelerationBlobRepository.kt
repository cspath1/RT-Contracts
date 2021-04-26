package com.radiotelescope.repository.acceleration

import com.radiotelescope.repository.location.Location
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [AccelerationBlob] Entity
 */
@Repository
interface IAccelerationBlobRepository : CrudRepository<AccelerationBlob, Long>