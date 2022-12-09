package com.radiotelescope.contracts.thresholds

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.FakeUserContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserThresholdsWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var thresholdsRepo: IThresholdsRepository

    private lateinit var admin: User

    val context = FakeUserContext()
    lateinit var factory: ThresholdsFactory
    lateinit var wrapper: UserThresholdsWrapper

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseThresholdsFactory(
                thresholdsRepo = thresholdsRepo
        )

        wrapper = UserThresholdsWrapper(
                context = context,
                factory = factory
        )

        admin = testUtil.createUser("admin@ycpradiotelescope.com")
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        testUtil.populateDefaultThresholds()
    }

    @Test
    fun adminRetrieve_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieve(
                sensorName = "WIND"
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun retrieve_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.retrieve(
                sensorName = "WIND"
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun adminRetrieveList_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieveList() {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun retrieveList_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.retrieveList {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun adminUpdate_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.update(
                sensorName = "WIND",
                maximum = 1.0
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun update_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.update(
                sensorName = "WIND",
                maximum = 1.0
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }
}