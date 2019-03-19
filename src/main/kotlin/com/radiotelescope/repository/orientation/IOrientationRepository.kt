package com.radiotelescope.repository.orientation

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [Orientation] Entity
 */
@Repository
interface IOrientationRepository : CrudRepository<Orientation, Long>