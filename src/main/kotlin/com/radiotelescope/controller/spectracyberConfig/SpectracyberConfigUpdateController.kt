package com.radiotelescope.controller.spectracyberConfig

import com.radiotelescope.contracts.spectracyberConfig.UserSpectracyberConfigWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.spectracyberConfig.UpdateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.controller.model.Result
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class SpectracyberConfigUpdateController(
        private val userWrapper: UserSpectracyberConfigWrapper,
        logger: Logger
) : BaseRestController(logger) {

    @CrossOrigin(value = ["http://localhost:8081"])
    @PutMapping(value = ["/api/spectracyberConfig/{spectracyberConfigId}"])
    fun execute(@PathVariable("spectracyberConfigId") spectracyberConfigId: Long,
                @RequestBody form: UpdateForm): Result {
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Update",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        }

        return result
    }
}