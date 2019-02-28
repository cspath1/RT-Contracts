package com.radiotelescope.controller

import com.radiotelescope.TestUtil
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import com.radiotelescope.controller.admin.user.AdminUserListController
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.FakeUserContext
import com.radiotelescope.security.UserContext
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.lang.RuntimeException


@RunWith(SpringRunner::class)
@SpringBootTest
@DataJpaTest
@ActiveProfiles("test")
internal class ExceptionLoggerTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var adminUserListController: AdminUserListController

    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var errorRepo: IErrorRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var userContext: UserContext

    private lateinit var logger: Logger

    @Before
    fun init() {
        userContext = FakeUserContext()

        logger = Logger(
                logRepo = logRepo,
                errorRepo = errorRepo,
                userRepo = userRepo,
                userContext = userContext
        )

        mockMvc = MockMvcBuilders.standaloneSetup(adminUserListController).setControllerAdvice(ExceptionLogger(logger)).build()
    }

    @Test
    fun testExceptionHandler() {
        Mockito.`when`(adminUserListController.execute(0, 15)).thenThrow(RuntimeException("An Unknown Exception Occurred"))

        mockMvc.perform(get("/api/users?page=0&size=15"))

        // There should be a log now
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), it.status)
        }
    }
}