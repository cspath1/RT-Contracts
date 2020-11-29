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
internal class RetrieveTest : AbstractSpringTest() {

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
        val (id, error) = Retrieve(
                spectracyberConfigId = spectracyberConfigId,
                spectracyberConfigRepo = spectracyberConfigRepo
        ).execute()

        assertNull(error)
        assertNotNull(id)
    }

    @Test
    fun testInvalidConstraints_Failure() {
        val (id, error) = Retrieve(
                spectracyberConfigId = spectracyberConfigId + 1,
                spectracyberConfigRepo = spectracyberConfigRepo
        ).execute()

        assertNotNull(error)
        assertNull(id)
    }
}