package com.radiotelescope.repository.log

import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Spring Repository for the [Log] Entity
 */
interface ILogRepository : PagingAndSortingRepository<Log, Long>