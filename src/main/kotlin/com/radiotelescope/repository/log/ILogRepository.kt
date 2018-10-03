package com.radiotelescope.repository.log

import org.springframework.data.repository.PagingAndSortingRepository

interface ILogRepository : PagingAndSortingRepository<Log, Long> {
}