package com.radiotelescope.controller.celestialBody

import com.radiotelescope.contracts.celestialBody.BaseCelestialBodyFactory
import com.radiotelescope.contracts.celestialBody.UserCelestialBodyWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

internal abstract class BaseCelestialBodyRestControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    // These will both be needed in all celestial body
    // controller tests, so instantiate them here
    private lateinit var wrapper: UserCelestialBodyWrapper
    private lateinit var factory: BaseCelestialBodyFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseCelestialBodyFactory(
                celestialBodyRepo = celestialBodyRepo,
                coordinateRepo = coordinateRepo
        )

        wrapper = UserCelestialBodyWrapper(
                getContext(),
                factory = factory
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserCelestialBodyWrapper {
        return wrapper
    }
}