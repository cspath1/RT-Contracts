package com.radiotelescope.service.heartbeat

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.heartbeatMonitor.HeartbeatMonitor
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import org.springframework.stereotype.Service
import java.util.*

/**
 * Concrete implementation of the [IHandleHeartbeatService] service. It will retrieve
 * the [HeartbeatMonitor] associated with a Radio Telescope (if one does not exist, it will
 * create a new one), and update the last communication time to the current time.
 *
 * @param radioTelescopeRepo the [IRadioTelescopeRepository]
 * @param heartbeatMonitorRepo the [IHeartbeatMonitorRepository]
 */
@Service
class HandleHeartbeatService(
        private val radioTelescopeRepo: IRadioTelescopeRepository,
        private val heartbeatMonitorRepo: IHeartbeatMonitorRepository
) : IHandleHeartbeatService {
    /**
     * Override of the [IHandleHeartbeatService.execute] method that will update the [HeartbeatMonitor]
     * associated with a particular Radio Telescope
     *
     * @param radioTelescopeId the Radio Telescope id
     * @return a [SimpleResult] object
     */
    override fun execute(radioTelescopeId: Long): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest(radioTelescopeId)?.let { return SimpleResult(null, it) } ?: let {
            // New radio telescope instances may not have an associated heartbeat
            // If this is the case, one must be created. If one, already exists,
            // we just need to retrieve it and update the date
            val monitor: HeartbeatMonitor

            if (heartbeatMonitorRepo.existsByRadioTelescopeId(radioTelescopeId)) {
                monitor = heartbeatMonitorRepo.findByRadioTelescopeId(radioTelescopeId)!!
            } else {
                monitor = HeartbeatMonitor()
                monitor.radioTelescope = radioTelescopeRepo.findById(radioTelescopeId).get()
            }

            monitor.lastCommunication = Date()
            heartbeatMonitorRepo.save(monitor)

            return SimpleResult(monitor.id, null)
        }
    }

    /**
     * Method in charge of validating that the radio telescope id exists
     *
     * @param radioTelescopeId the Radio Telescope id
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(radioTelescopeId: Long): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!radioTelescopeRepo.existsById(radioTelescopeId))
            errors.put(ErrorTag.RADIO_TELESCOPE_ID, "Radio Telescope #$radioTelescopeId could not be found")

        return if (errors.isEmpty) null else errors
    }
}