package com.radiotelescope.controller.sensorStatus

import com.radiotelescope.contracts.sensorStatus.UserSensorStatusWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.sensorStatus.CreateForm
import com.radiotelescope.contracts.sensorStatus.Create
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.log.Log
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle Video File creation
 *
 * @param sensorStatusWrapper the [UserSensorStatusWrapper]
 * @param logger the [Logger] service
 */
@RestController
class SensorStatusCreateController(
        private val sensorStatusWrapper: UserSensorStatusWrapper,
        private val profile: Profile,
        logger: Logger
) : BaseRestController(logger) {

    // Get the secret used by the control room
    @Value(value = "\${radio-telescope.control-room-uuid-secret}")
    lateinit var uuid: String

    /**
     * Execute method that is in charge of adapting the [CreateForm]
     * into a [Create.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserSensorStatusWrapper.create] method.
     * Based on the result of this call, the method will either respond with
     * the data or the errors from the method call.
     */
    @PostMapping(value = ["/api/sensor-status"])
    fun execute(@RequestBody form: CreateForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
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
        // Otherwise, execute the wrapper command
        let {
            val response = sensorStatusWrapper.create(
                    request = form.toRequest(),
                    uuid = uuid,
                    profile = profile.toString()
            ).execute()
            // If the command was a success
            response.success?.let { data ->
                // Create success log
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
            // Otherwise, there was an error
            response.error?.let { errors ->
                // Create error logs
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