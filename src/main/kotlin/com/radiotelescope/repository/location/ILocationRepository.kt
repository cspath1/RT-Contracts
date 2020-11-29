package com.radiotelescope.repository.location

import com.radiotelescope.repository.location.Location
import org.springframework.data.repository.CrudRepository


interface ILocationRepository : CrudRepository<Location, Long>