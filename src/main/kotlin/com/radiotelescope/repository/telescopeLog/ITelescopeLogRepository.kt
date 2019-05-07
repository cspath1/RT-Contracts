package com.radiotelescope.repository.telescopeLog

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ITelescopeLogRepository : PagingAndSortingRepository<TelescopeLog, Long>