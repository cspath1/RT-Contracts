package com.radiotelescope.repository.heartbeatMonitor

import com.radiotelescope.AbstractSpringTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class HeartbeatMonitorTest : AbstractSpringTest() {
    @Autowired
    private lateinit var heartBeatMonitorRepo: IHeartbeatMonitorRepository

    @Test
    fun testFindByRadioTelescopeId() {
        val heartbeatInfo = heartBeatMonitorRepo.findByRadioTelescopeId(1L)

        assertNotNull(heartbeatInfo)
        assertEquals(1L, heartbeatInfo!!.radioTelescope.getId())
    }

    @Test
    fun testExistsByRadioTelescopeId() {
        val heartbeatMonitorExists = heartBeatMonitorRepo.existsByRadioTelescopeId(1L)

        assertTrue(heartbeatMonitorExists)
    }
}