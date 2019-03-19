package com.radiotelescope.repository.orientation

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface IOrientationRepository : CrudRepository<Orientation, Long>