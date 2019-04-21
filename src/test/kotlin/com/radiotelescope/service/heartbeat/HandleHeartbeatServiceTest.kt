package com.radiotelescope.service.heartbeat

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql", "classpath:sql/seedTelescopeWithoutHeartbeat.sql"])
internal class HandleHeartbeatServiceTest : AbstractSpringTest() {
    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var heartbeatMonitorRepo: IHeartbeatMonitorRepository

    private lateinit var handleHeartbeatService: IHandleHeartbeatService

    @Before
    fun setUp() {
        handleHeartbeatService = HandleHeartbeatService(
                radioTelescopeRepo = radioTelescopeRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo
        )
    }

    @Test
    fun testExistingHeartbeatMonitor_Success() {
        val timeBeforeWait = Date()

        // Wait 5 seconds
        Thread.sleep(5000)

        val (id, errors) = handleHeartbeatService.execute(1L)

        assertNotNull(id)
        assertNull(errors)

        // The heartbeat monitor's last communication should now be
        // after the above time
        val theMonitor = heartbeatMonitorRepo.findByRadioTelescopeId(1L)

        assertNotNull(theMonitor)
        assertTrue(theMonitor!!.lastCommunication > timeBeforeWait)
    }

    @Test
    fun testNewHeartbeatMonitor_Success() {
        val timeBeforeWait = Date()

        // Wait 5 seconds
        Thread.sleep(5000)

        val (id, errors) = handleHeartbeatService.execute(2L)

        assertNotNull(id)
        assertNull(errors)

        // The heartbeat monitor's last communication should now be
        // after the above time
        val theMonitor = heartbeatMonitorRepo.findByRadioTelescopeId(2L)

        assertNotNull(theMonitor)
        assertTrue(theMonitor!!.lastCommunication > timeBeforeWait)
        assertEquals(2, heartbeatMonitorRepo.count())
    }

    @Test
    fun testInvalidRadioTelescopeId_Failure() {
        val (id, errors) = handleHeartbeatService.execute(311L)

        assertNull(id)
        assertNotNull(errors)

        // Ensure the reason for failure was as expected
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.RADIO_TELESCOPE_ID].isNotEmpty())
    }
}