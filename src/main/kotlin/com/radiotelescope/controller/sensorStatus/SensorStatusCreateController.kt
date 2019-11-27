package com.radiotelescope.controller.sensorStatus

import com.radiotelescope.contracts.sensorStatus.UserSensorStatusWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.sensorStatus.CreateForm
import com.radiotelescope.repository.log.Log
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SensorStatusCreateController(
        private val sensorStatusWrapper: UserSensorStatusWrapper,
        logger: Logger
) : BaseRestController(logger) {

    @Value(value = "\${radio-telescope.control-room-uuid-secret}")
    lateinit var uuid: String

    @PostMapping(value = ["/api/sensor-data"])
    fun execute(@RequestBody form: CreateForm): Result {
        form.validateRequest()?.let {
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.SENSOR_STATUS,
                            action = "Sensor Status Creation",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        }?:
        let {
            val response = sensorStatusWrapper.create(
                    request = form.toRequest(),
                    uuid = uuid
            ).execute()

            response.success?.let { data ->
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.SENSOR_STATUS,
                                action = "Sensor Status Creation",
                                affectedRecordId = data,
                                status = HttpStatus.OK.value()
                        )
                )
                result = Result(data = data)
            }

            response.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.SENSOR_STATUS,
                                action = "Sensor Status Creation",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = errors.toStringMap()
                )
                result = Result(errors = errors.toStringMap())
            }
        }

        return result
    }
}