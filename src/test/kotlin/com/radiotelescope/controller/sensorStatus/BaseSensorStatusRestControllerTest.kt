package com.radiotelescope.controller.sensorStatus

import com.radiotelescope.contracts.sensorStatus.BaseSensorStatusFactory
import com.radiotelescope.contracts.sensorStatus.SensorStatusFactory
import com.radiotelescope.contracts.sensorStatus.UserSensorStatusWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseSensorStatusRestControllerTest: BaseRestControllerTest() {
    @Autowired
    private lateinit var sensorStatusRepo: ISensorStatusRepository

    private lateinit var wrapper: UserSensorStatusWrapper
    private lateinit var factory: SensorStatusFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseSensorStatusFactory(
                sensorStatusRepo = sensorStatusRepo
        )

        wrapper = UserSensorStatusWrapper(
                context = getContext(),
                factory = factory
        )
    }

    fun getWrapper(): UserSensorStatusWrapper {
        return wrapper
    }
}