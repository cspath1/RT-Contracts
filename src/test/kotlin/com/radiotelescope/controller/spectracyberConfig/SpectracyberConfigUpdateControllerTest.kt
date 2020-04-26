package com.radiotelescope.controller.spectracyberConfig

import com.radiotelescope.contracts.spectracyberConfig.BaseSpectracyberConfigFactory
import com.radiotelescope.contracts.spectracyberConfig.UserSpectracyberConfigWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.controller.model.spectracyberConfig.UpdateForm
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
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

@DataJpaTest
@RunWith(SpringRunner::class)
internal class SpectracyberConfigUpdateControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var spectracyberConfigRepo: ISpectracyberConfigRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var spectracyberConfigUpdateController: SpectracyberConfigUpdateController

    private lateinit var baseForm: UpdateForm
    private lateinit var updateForm: UpdateForm
    private lateinit var user: User

    private var userContext = getContext()

    @Before
    override fun init() {
        super.init()

        spectracyberConfigUpdateController = SpectracyberConfigUpdateController(
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

        user = testUtil.createUser("jhorne@ycp.edu")
        // simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.USER)

        baseForm = UpdateForm(
                id = 1,
                mode = "CONTINUUM",
                integrationTime = 0.4,
                offsetVoltage = 0.4095,
                IFGain = 11.0,
                DCGain = 5,
                bandwidth = 1300
        )
    }

    @Test
    fun testUserResponseOwnRecord_Success() {
        testUtil.createDefaultSpectracyberConfig()

        // set the update form to the newest entry in the repo
        // ensures grabbing correct record id
        updateForm = baseForm.copy(id = spectracyberConfigRepo.findAll().first().id)

        // update the spectracyber config record
        val result = spectracyberConfigUpdateController.execute(
                form = updateForm
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
        testUtil.createDefaultSpectracyberConfig()
        testUtil.createDefaultSpectracyberConfig()

        updateForm = baseForm.copy(id = spectracyberConfigRepo.findAll().first().id)

        // attempt to update the spectracyber config record
        val result = spectracyberConfigUpdateController.execute(
                form = updateForm
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

        testUtil.createDefaultSpectracyberConfig()
        testUtil.createDefaultSpectracyberConfig()

        updateForm = baseForm.copy(id = spectracyberConfigRepo.findAll().first().id)

        // attempt to update the spectracyber config record
        val result = spectracyberConfigUpdateController.execute(
                form = updateForm
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
        // log the user out
        userContext.logout()

        // update the spectracyber config record
        val result = spectracyberConfigUpdateController.execute(
                form = baseForm
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