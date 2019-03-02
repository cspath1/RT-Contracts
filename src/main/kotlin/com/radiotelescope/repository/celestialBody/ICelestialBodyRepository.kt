package com.radiotelescope.repository.celestialBody

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository Interface for the [CelestialBody] Entity
 */
@Repository
interface ICelestialBodyRepository : PagingAndSortingRepository<CelestialBody, Long>