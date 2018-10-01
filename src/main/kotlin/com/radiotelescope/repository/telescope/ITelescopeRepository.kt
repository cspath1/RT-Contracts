package com.radiotelescope.repository.telescope

import org.springframework.data.repository.CrudRepository

interface ITelescopeRepository : CrudRepository<Telescope, Long> {
}