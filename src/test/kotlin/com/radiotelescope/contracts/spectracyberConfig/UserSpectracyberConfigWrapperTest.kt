package com.radiotelescope.contracts.spectracyberConfig

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig
import com.radiotelescope.repository.user.IUserRepository
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
internal class UserSpectracyberConfigWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var spectracyberConfigRepo: ISpectracyberConfigRepository

    private lateinit var user: User
    private lateinit var admin: User

    private lateinit var theSpectracyberConfig: SpectracyberConfig

    val context = FakeUserContext()
    lateinit var factory: SpectracyberConfigFactory
    lateinit var wrapper: UserSpectracyberConfigWrapper

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseSpectracyberConfigFactory(
                spectracyberConfigRepo = spectracyberConfigRepo
        )

        wrapper = UserSpectracyberConfigWrapper(
                context = context,
                factory = factory,
                userRepo = userRepo
        )

        // Create user and admin with default roles
        user = testUtil.createUser("jhorne@ycp.edu")
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        admin = testUtil.createUser("admin@ycpradiotelescope.com")
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        // Persist a default Spectracyber Config
        theSpectracyberConfig = testUtil.createDefaultSpectracyberConfig()
    }

    @Test
    fun specifiedUserRetrieve_Success() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
                userId = user.id,
                spectracyberConfigId = theSpectracyberConfig.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun specifiedUserRetrieve_OtherUserId_Failure() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
                userId = -1L,
                spectracyberConfigId = theSpectracyberConfig.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun adminRetrieve_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieve(
                userId = user.id,
                spectracyberConfigId = theSpectracyberConfig.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.retrieve(
                userId = user.id,
                spectracyberConfigId = theSpectracyberConfig.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun specifiedUserUpdate_Success() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                userId = user.id,
                request = Update.Request(
                        theSpectracyberConfig.id,
                        "SPECTRAL",
                        0.3,
                        0.0,
                        10.0,
                        1,
                        1300
                )

        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun specifiedUserUpdate_OtherUserId_Failure() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
                userId = -1L,
                request = Update.Request(
                        theSpectracyberConfig.id,
                        "SPECTRAL",
                        0.3,
                        0.0,
                        10.0,
                        1,
                        1300
                )
        ) {
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
                userId = user.id,
                request = Update.Request(
                        theSpectracyberConfig.id,
                        "SPECTRAL",
                        0.3,
                        0.0,
                        10.0,
                        1,
                        1300
                )
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testUpdate_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.update(
                userId = user.id,
                request = Update.Request(
                        theSpectracyberConfig.id,
                        "SPECTRAL",
                        0.3,
                        0.0,
                        10.0,
                        1,
                        1300
                )
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }
}