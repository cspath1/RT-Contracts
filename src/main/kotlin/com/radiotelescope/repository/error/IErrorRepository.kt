package com.radiotelescope.repository.error

import org.springframework.data.repository.CrudRepository

/**
 * Spring Repository for the [Error] Entity
 */
interface IErrorRepository : CrudRepository<Error, Long>