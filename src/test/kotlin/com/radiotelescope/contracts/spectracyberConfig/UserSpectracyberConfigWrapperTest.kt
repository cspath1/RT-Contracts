package com.radiotelescope.contracts.spectracyberConfig

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
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
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserSpectracyberConfigWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var spectracyberConfigRepo: ISpectracyberConfigRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var otherUser: User
    private lateinit var admin: User

    private lateinit var theSpectracyberConfig: SpectracyberConfig
    private lateinit var theAppointment: Appointment

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
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )

        // Create user, other user, and admin with default roles
        user = testUtil.createUser("jhorne@ycp.edu")
        testUtil.createUserRoleForUser(user, UserRole.Role.USER, true)

        otherUser = testUtil.createUser("otheruser@ycp.edu")
        testUtil.createUserRoleForUser(otherUser, UserRole.Role.USER, true)

        admin = testUtil.createUser("admin@ycpradiotelescope.com")
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)

        // Persist a default appointment with a default spectracyber config
        theAppointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis()),
                endTime = Date(System.currentTimeMillis() + 60000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.FREE_CONTROL
        )
        theSpectracyberConfig = theAppointment.spectracyberConfig!!
    }

    @Test
    fun userRetrieve_Success() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
                spectracyberConfigId = theSpectracyberConfig.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun specifiedUserRetrieve_OtherUserId_Failure() {
        context.login(otherUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.retrieve(
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
                spectracyberConfigId = theSpectracyberConfig.id
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
                spectracyberConfigId = theSpectracyberConfig.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
    }

    @Test
    fun userUpdate_Success() {
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
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
    fun userUpdate_OtherUserId_Failure() {
        context.login(otherUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.update(
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
    fun update_NotLoggedIn_Failure() {
        // Do not log the user in
        val error = wrapper.update(
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