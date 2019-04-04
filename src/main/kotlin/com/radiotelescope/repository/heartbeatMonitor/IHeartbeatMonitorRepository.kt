package com.radiotelescope.repository.heartbeatMonitor

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface IHeartbeatMonitorRepository : CrudRepository<HeartbeatMonitor, Long> {
    fun existsByRadioTelescopeId(id: Long): Boolean
    fun findByRadioTelescopeId(id: Long): HeartbeatMonitor?
}