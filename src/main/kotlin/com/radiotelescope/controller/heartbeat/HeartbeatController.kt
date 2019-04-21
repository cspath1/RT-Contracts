package com.radiotelescope.controller.heartbeat

import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.controller.model.Result
import com.radiotelescope.service.heartbeat.IHandleHeartbeatService
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle heartbeats from local servers
 *
 * @param handleHeartbeatService the [HandleHeartbeatService] service
 */
@RestController
class HeartbeatController(
        private val handleHeartbeatService: IHandleHeartbeatService
) {
    @GetMapping(value = ["/api/heartbeat/{radioTelescopeId}"])
    fun execute(@PathVariable("radioTelescopeId") radioTelescopeId: Long): Result {
        val result = handleHeartbeatService.execute(radioTelescopeId)

        return if (result.success != null) {
            Result(data = result.success)
        } else {
            Result(errors = result.error!!.toStringMap())
        }
    }
}