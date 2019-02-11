package com.radiotelescope.controller.spring

import com.google.common.collect.HashMultimap
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.FakeUserContext
import com.radiotelescope.toStringMap
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class LoggerTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var errorRepo: IErrorRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var testUtil: TestUtil

    private lateinit var logger: Logger

    private val userContext = FakeUserContext()

    @Before
    fun setUp() {
        // Instantiate the Logger object
        logger = Logger(
                logRepo = logRepo,
                errorRepo = errorRepo,
                userRepo = userRepo,
                userContext = userContext
        )
    }

    @Test
    fun testCreateSuccessLog_NoUserId_Success() {
        // The user is not logged in, so the log generated
        // should have a null user id
        logger.createSuccessLog(
                info = Logger.Info(
                        affectedTable = Log.AffectedTable.USER,
                        action = "User Registration",
                        timestamp = Date(),
                        affectedRecordId = null
                )
        )

        assertEquals(1, logRepo.count())

        val iterable = logRepo.findAll()
        iterable.forEach {
            assertTrue(it.isSuccess)
            assertEquals(Log.AffectedTable.USER, it.affectedTable)
            assertEquals("User Registration", it.action)
            assertNull(it.affectedRecordId)
            assertNull(it.user)
            assertEquals(0, it.errors.size)
        }
    }

    @Test
    fun testCreateSuccessLog_UserId_Success() {
        val user: User = testUtil.createUser("lferree@ycp.edu")
        // Simulate a login
        userContext.login(user.id)

        // The user is now logged in, so the log generated
        // should have a populated user id
        logger.createSuccessLog(
                info = Logger.Info(
                        affectedTable = Log.AffectedTable.APPOINTMENT,
                        action = "User Registration",
                        timestamp = Date(),
                        affectedRecordId = null
                )
        )

        assertEquals(1, logRepo.count())

        val iterable = logRepo.findAll()
        iterable.forEach {
            assertTrue(it.isSuccess)
            assertEquals(Log.AffectedTable.APPOINTMENT, it.affectedTable)
            assertEquals("User Registration", it.action)
            assertNull(it.affectedRecordId)
            assertEquals(user, it.user)
            assertEquals(0, it.errors.size)
        }
    }

    @Test
    fun testCreateErrorLogs_NoUserId_Success() {
        // Create our errors map
        val errors = HashMultimap.create<ErrorTag, String>()

        // Add two errors to the map
        errors.put(ErrorTag.FIRST_NAME, "First name may not be blank")
        errors.put(ErrorTag.LAST_NAME, "Last name may not be blank")

        logger.createErrorLogs(
                info = Logger.Info(
                        affectedTable = Log.AffectedTable.USER,
                        action = "Appointment Creation",
                        timestamp = Date(),
                        affectedRecordId = null
                ),
                errors = errors.toStringMap()
        )

        // There should be one log and 2 errors
        assertEquals(1, logRepo.count())
        assertEquals(2, errorRepo.count())

        val iterableLogs = logRepo.findAll()
        iterableLogs.forEach {
            assertFalse(it.isSuccess)
            assertEquals(Log.AffectedTable.USER, it.affectedTable)
            assertEquals("Appointment Creation", it.action)
            assertNull(it.affectedRecordId)
            assertNull(it.user)
            assertEquals(2, it.errors.size)
        }

        val iterableErrors = errorRepo.findAll()
        iterableErrors.forEach {
            assertEquals(it.log, iterableLogs.toMutableList()[0])
        }
    }

    @Test
    fun testCreateErrorLogs_UserId_Success() {
        val user: User = testUtil.createUser("lferree@ycp.edu")
        user.id = 1L
        // Simulate a login
        userContext.login(user.id)

        // Create our errors map
        val errors = HashMultimap.create<ErrorTag, String>()

        // Add two errors to the map
        errors.put(ErrorTag.FIRST_NAME, "First name may not be blank")
        errors.put(ErrorTag.LAST_NAME, "Last name may not be blank")

        logger.createErrorLogs(
                info = Logger.Info(
                        affectedTable = Log.AffectedTable.USER,
                        action = "User Registration",
                        timestamp = Date(),
                        affectedRecordId = null
                ),
                errors = errors.toStringMap()
        )

        // There should be one log and 2 errors
        assertEquals(1, logRepo.count())
        assertEquals(2, errorRepo.count())

        val iterableLogs = logRepo.findAll()
        iterableLogs.forEach {
            assertFalse(it.isSuccess)
            assertEquals(Log.AffectedTable.USER, it.affectedTable)
            assertEquals("User Registration", it.action)
            assertNull(it.affectedRecordId)
            assertEquals(user, it.user)
            assertEquals(2, it.errors.size)
        }

        val iterableErrors = errorRepo.findAll()
        iterableErrors.forEach {
            assertEquals(it.log, iterableLogs.toMutableList()[0])
        }
    }
}