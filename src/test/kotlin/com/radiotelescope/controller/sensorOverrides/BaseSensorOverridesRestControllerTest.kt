package com.radiotelescope.controller.sensorOverrides

import com.radiotelescope.contracts.sensorOverrides.BaseSensorOverridesFactory
import com.radiotelescope.contracts.sensorOverrides.SensorOverridesFactory
import com.radiotelescope.contracts.sensorOverrides.UserSensorOverridesWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseSensorOverridesRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var sensorOverridesRepo: ISensorOverridesRepository

    private lateinit var wrapper: UserSensorOverridesWrapper
    private lateinit var factory: SensorOverridesFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseSensorOverridesFactory(
                sensorOverridesRepo = sensorOverridesRepo
        )

        wrapper = UserSensorOverridesWrapper(
                context = getContext(),
                factory = factory
        )
    }

    fun getWrapper(): UserSensorOverridesWrapper {
        return wrapper
    }
}