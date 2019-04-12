package com.radiotelescope.repository.telescope

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository Interface for the [RadioTelescope] Entity
 */
@Repository
interface IRadioTelescopeRepository : CrudRepository<RadioTelescope, Long>