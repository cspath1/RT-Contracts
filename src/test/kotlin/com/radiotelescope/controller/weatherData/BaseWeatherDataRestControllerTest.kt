package com.radiotelescope.controller.weatherData

import com.radiotelescope.contracts.weatherData.BaseWeatherDataFactory
import com.radiotelescope.contracts.weatherData.UserWeatherDataWrapper
import com.radiotelescope.contracts.weatherData.WeatherDataFactory
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseWeatherDataRestControllerTest: BaseRestControllerTest() {
    @Autowired
    private lateinit var weatherDataRepo: IWeatherDataRepository

    private lateinit var wrapper: UserWeatherDataWrapper
    private lateinit var factory: WeatherDataFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseWeatherDataFactory(
                weatherDataRepo = weatherDataRepo
        )

        wrapper = UserWeatherDataWrapper(
                context = getContext(),
                factory = factory
        )
    }

    fun getWrapper(): UserWeatherDataWrapper {
        return wrapper
    }
}