package com.radiotelescope.repository.telescope

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ITelescopeRepository : CrudRepository<Telescope, Long> {
}