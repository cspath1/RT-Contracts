package com.radiotelescope.contracts.log

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.FakeUserContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class AdminLogWrapperTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var user: User

    private val context = FakeUserContext()
    private lateinit var factory: BaseLogFactory
    private lateinit var wrapper: AdminLogWrapper

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("cspath1@ycp.edu")

        // Persist a log
        testUtil.createLog(
                userId = 1L,
                affectedRecordId = 1L,
                affectedTable = Log.AffectedTable.USER,
                action = "User Registered",
                timestamp = Date(),
                isSuccess = true
        )

        factory = BaseLogFactory(
                logRepo = logRepo
        )

        wrapper = AdminLogWrapper(
                context = context,
                factory = factory
        )
    }

    @Test
    fun testList_Admin_Success() {
        // Simulate a login as an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        val error = wrapper.list(
                pageable = PageRequest.of(0,5)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testList_NotLoggedIn_Failure() {
        // Do not log the user in

        val error = wrapper.list(
                pageable = PageRequest.of(0, 10)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testList_NotAdmin_Failure() {
        // Log the user in as something other than an admin
        context.login(user.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.STUDENT))

        val error = wrapper.list(
                pageable = PageRequest.of(0, 10)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }
}