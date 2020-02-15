package com.radiotelescope.contracts.spectracyberConfig

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UpdateTest : AbstractSpringTest() {

    @Autowired
    private lateinit var spectracyberConfigRepo: ISpectracyberConfigRepository

    private lateinit var spectracyberConfig: SpectracyberConfig

    private var spectracyberConfigId = -1L

    @Before
    fun init() {
        // Persist the SpectracyberConfig
        spectracyberConfig = testUtil.createDefaultSpectracyberConfig()

        spectracyberConfigId = spectracyberConfig.id
    }

    @Test
    fun testValidConstraints_Success() {
        val (id, error) = Update(
                request = Update.Request(
                        id = spectracyberConfigId,
                        mode = "CONTINUUM",
                        integrationTime = 0.4,
                        offsetVoltage = 0.1,
                        IFGain = 11.0,
                        DCGain = 2,
                        bandwidth = 1300
                ),
                spectracyberConfigRepo = spectracyberConfigRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(id)
    }

    @Test
    fun testInvalidConstraintsLessThanZero_Failure() {
        // Update spectracyber config with negative value
        val (id, error) = Update(
                request = Update.Request(
                        id = spectracyberConfigId,
                        mode = "CONTINUUM",
                        integrationTime = -0.1,
                        offsetVoltage = 0.1,
                        IFGain = 11.0,
                        DCGain = 2,
                        bandwidth = 1300
                ),
                spectracyberConfigRepo = spectracyberConfigRepo
        ).execute()

        // Should have failed
        assertNotNull(error)
        assertNull(id)
    }

    @Test
    fun testInvalidConstraintsBadMode_Failure() {
        // Update spectracyber config with incorrect mode
        val (id, error) = Update(
                request = Update.Request(
                        id = spectracyberConfigId,
                        mode = "INCORRECT_MODE",
                        integrationTime = 0.1,
                        offsetVoltage = 0.1,
                        IFGain = 11.0,
                        DCGain = 2,
                        bandwidth = 1300
                ),
                spectracyberConfigRepo = spectracyberConfigRepo
        ).execute()

        // Should have failed
        assertNotNull(error)
        assertNull(id)
    }
}