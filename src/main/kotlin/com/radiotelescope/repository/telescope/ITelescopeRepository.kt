package com.radiotelescope.repository.telescope

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository Interface for the [Telescope] Entity
 */
@Repository
interface ITelescopeRepository : CrudRepository<Telescope, Long>