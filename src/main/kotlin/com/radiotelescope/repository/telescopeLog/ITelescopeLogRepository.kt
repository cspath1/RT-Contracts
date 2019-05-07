package com.radiotelescope.repository.telescopeLog

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [TelescopeLog] Entity
 */
@Repository
interface ITelescopeLogRepository : PagingAndSortingRepository<TelescopeLog, Long>