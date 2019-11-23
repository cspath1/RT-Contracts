package com.radiotelescope.controller.sensorStatus

import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.spring.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RestController

@RestController
class SensorStatusCreateController(
    logger: Logger
) : BaseRestController(logger) {

    @Value(value = "\${radio-telescope.control-room-uuid-secret}")
    lateinit var id: String
}