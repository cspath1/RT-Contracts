package com.radiotelescope.repository.coordinate

import org.springframework.data.repository.CrudRepository

/**
 * Spring Repository for the [Coordinate] Entity
 */
interface ICoordinateRepository : CrudRepository<Coordinate, Long>