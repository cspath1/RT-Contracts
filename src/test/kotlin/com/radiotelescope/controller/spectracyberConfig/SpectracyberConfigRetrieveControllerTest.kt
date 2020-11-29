package com.radiotelescope.controller.spectracyberConfig

import com.radiotelescope.contracts.spectracyberConfig.BaseSpectracyberConfigFactory
import com.radiotelescope.contracts.spectracyberConfig.UserSpectracyberConfigWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class SpectracyberConfigRetrieveControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var spectracyberConfigRepo: ISpectracyberConfigRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var spectracyberConfigRetrieveController: SpectracyberConfigRetrieveController

    private lateinit var user: User
    private lateinit var otherUser: User

    private lateinit var theSpectracyberConfig: SpectracyberConfig
    private lateinit var theAppointment: Appointment

    private var userContext = getContext()

    @Before
    override fun init() {
        super.init()

        spectracyberConfigRetrieveController = SpectracyberConfigRetrieveController(
                spectracyberConfigWrapper = UserSpectracyberConfigWrapper(
                        context = userContext,
                        factory = BaseSpectracyberConfigFactory(
                                spectracyberConfigRepo = spectracyberConfigRepo
                        ),
                        userRepo = userRepo,
                        appointmentRepo = appointmentRepo
                ),
                logger = getLogger()
        )

        otherUser = testUtil.createUser("otheruser@ycp.edu")

        user = testUtil.createUser("jhorne@ycp.edu")
        // simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

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
    fun testUserResponseOwnRecord_Success() {
        // retrieve the spectracyber config record
        val result = spectracyberConfigRetrieveController.execute(
                spectracyberConfigId = theSpectracyberConfig.id
        )

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testUserResponseOtherRecord_Failure() {
        // Persist an appointment with a default spectracyber config
        val otherAppointment = testUtil.createAppointment(
                user = otherUser,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis()),
                endTime = Date(System.currentTimeMillis() + 60000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.FREE_CONTROL
        )
        val otherSpectracyberConfig = otherAppointment.spectracyberConfig!!

        // attempt to retrieve the spectracyber config record
        val result = spectracyberConfigRetrieveController.execute(
                spectracyberConfigId = otherSpectracyberConfig.id
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertNotNull(result.errors)

        // ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }

    @Test
    fun testAdminResponseOtherRecord_Success() {
        // make the user an admin
        userContext.currentRoles.add(UserRole.Role.ADMIN)

        // Persist an appointment with a default spectracyber config
        val otherAppointment = testUtil.createAppointment(
                user = otherUser,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis()),
                endTime = Date(System.currentTimeMillis() + 60000L),
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.FREE_CONTROL
        )
        val otherSpectracyberConfig = otherAppointment.spectracyberConfig!!

        // retrieve the spectracyber config record
        val result = spectracyberConfigRetrieveController.execute(
                spectracyberConfigId = otherSpectracyberConfig.id
        )

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testValidForm_FailedAuthenticationResponse() {
        testUtil.createDefaultSpectracyberConfig()

        // log the user out
        userContext.logout()

        // attempt to retrieve the spectracyber config record
        val result = spectracyberConfigRetrieveController.execute(
                spectracyberConfigId = spectracyberConfigRepo.findAll().first().id
        )

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}