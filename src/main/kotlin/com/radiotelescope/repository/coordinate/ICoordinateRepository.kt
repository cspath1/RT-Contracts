package com.radiotelescope.repository.coordinate

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [Coordinate] Entity
 */
@Repository
interface ICoordinateRepository : CrudRepository<Coordinate, Long>