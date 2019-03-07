package com.radiotelescope.repository.error

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [Error] Entity
 */
@Repository
interface IErrorRepository : CrudRepository<Error, Long>