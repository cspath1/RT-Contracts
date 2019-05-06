package com.radiotelescope.repository.log

import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [Log] Entity
 */
@Repository
interface ILogRepository : PagingAndSortingRepository<Log, Long>, JpaSpecificationExecutor<Log>