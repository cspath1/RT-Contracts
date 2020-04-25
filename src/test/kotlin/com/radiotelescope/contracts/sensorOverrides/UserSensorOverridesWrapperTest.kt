package com.radiotelescope.contracts.sensorOverrides

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
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
internal class UserSensorOverridesWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var sensorOverridesRepo: ISensorOverridesRepository

    private lateinit var admin: User

    val context = FakeUserContext()
    lateinit var factory: SensorOverridesFactory
    lateinit var wrapper: UserSensorOverridesWrapper

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseSensorOverridesFactory(
                sensorOverridesRepo = sensorOverridesRepo
        )

        wrapper = UserSensorOverridesWrapper(
                context = context,
                factory = factory
        )

        // Create admin with default roles
        admin = testUtil.createUser("admin@ycpradiotelescope.com")
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        testUtil.populateDefaultSensorOverrides()
    }

    @Test
    fun adminRetrieveList_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieveList {
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
                sensorName = "GATE",
                overridden = true
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
                sensorName = "GATE",
                overridden = true
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }
}