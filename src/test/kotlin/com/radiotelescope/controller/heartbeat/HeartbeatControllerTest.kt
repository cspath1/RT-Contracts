package com.radiotelescope.controller.heartbeat

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.heartbeatMonitor.IHeartbeatMonitorRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.service.heartbeat.HandleHeartbeatService
import com.radiotelescope.service.heartbeat.IHandleHeartbeatService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql", "classpath:sql/seedTelescopeWithoutHeartbeat.sql"])
internal class HeartbeatControllerTest : AbstractSpringTest() {
    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var heartbeatMonitorRepo: IHeartbeatMonitorRepository

    private lateinit var handleHeartbeatService: IHandleHeartbeatService
    private lateinit var heartbeatController: HeartbeatController

    @Before
    fun setUp() {
        handleHeartbeatService = HandleHeartbeatService(
                radioTelescopeRepo = radioTelescopeRepo,
                heartbeatMonitorRepo = heartbeatMonitorRepo
        )

        heartbeatController = HeartbeatController(handleHeartbeatService)
    }

    @Test
    fun testSuccess() {
        val result = heartbeatController.execute(1L)

        assertNotNull(result.data)
        assertNotNull(result.status)
        assertEquals(result.status, HttpStatus.OK)
        assertNull(result.errors)
    }

    @Test
    fun testFailure() {
        val result = heartbeatController.execute(311L)

        assertNotNull(result.errors)
        assertNull(result.data)
        assertNotNull(result.status)
        assertEquals(result.status, HttpStatus.BAD_REQUEST)
    }
}