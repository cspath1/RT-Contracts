package com.radiotelescope.contracts.spectracyberConfig

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseSpectracyberConfigFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var spectracyberConfigRepo: ISpectracyberConfigRepository

    private lateinit var factory: BaseSpectracyberConfigFactory

    private val request = Update.Request(
            id = 1,
            DCGain = 1,
            IFGain = 1.0,
            bandwidth = 1,
            integrationTime = 1.0,
            mode = "Unknown",
            offsetVoltage = 1.0
    )

    @Before
    fun init() {
        factory = BaseSpectracyberConfigFactory(
                spectracyberConfigRepo = spectracyberConfigRepo
        )
    }

    @Test
    fun updateSpectracyberConfig() {
        val cmd = factory.update(
                request = request
        )

        assertTrue(cmd is Update)
    }

    @Test
    fun retrieveSpectracyberConfig() {
        val cmd = factory.retrieve(
                spectracyberConfigId = 1
        )

        assertTrue(cmd is Retrieve)
    }
}