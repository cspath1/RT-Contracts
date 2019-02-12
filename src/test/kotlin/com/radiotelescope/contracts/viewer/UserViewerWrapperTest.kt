package com.radiotelescope.contracts.viewer

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.security.FakeUserContext
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
@ActiveProfiles(value = ["test"])
internal class UserViewerWrapperTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private lateinit var user: User
    private lateinit var admin: User
    private lateinit var appointment: Appointment

    private val context = FakeUserContext()
    private lateinit var factory: BaseViewerFactory
    private lateinit var wrapper: UserViewerWrapper

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("rpim@ycp.edu")
        admin = testUtil.createUser("rpim1@ycp.edu")

        // Persist an appointment for the user
        appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(System.currentTimeMillis() +   200000L),
                isPublic = false,
                status = Appointment.Status.SCHEDULED
        )

        factory = BaseViewerFactory(
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )

        wrapper = UserViewerWrapper(
                appointmentRepo = appointmentRepo,
                factory = factory,
                context = context
        )
    }

    @Test
    fun testValidConstraints_Success(){
        // Simulate a login
        context.login(user.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.RESEARCHER)

        val error = wrapper.sharePrivateAppointment(
                request = SharePrivateAppointment.Request(
                        userId = user.id,
                        appointmentId = appointment.id
                )
        ){
            assertNotNull(it.success)
            assertNull(it.error)
        }

        // Make sure it was a success
        assertNull(error)
    }

    @Test
    fun testAdmin_Success(){
        // Simulate a login
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.sharePrivateAppointment(
                request = SharePrivateAppointment.Request(
                        userId = user.id,
                        appointmentId = appointment.id
                )
        ){
            assertNotNull(it.success)
            assertNull(it.error)
        }

        // Make sure it was a success
        assertNull(error)
    }

    @Test
    fun testResearcher_NotLogIn_Failure(){
        // Don't simulate a login

        val error = wrapper.sharePrivateAppointment(
                request = SharePrivateAppointment.Request(
                        userId = user.id,
                        appointmentId = appointment.id
                )
        ){
            assertNull(it.success)
            assertNotNull(it.error)
        }

        // Make sure it was a failure and the correct reason
        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testResearcher_NotOwner_Failure(){
        // Simulate a login
        context.login(123L)
        context.currentRoles.add(UserRole.Role.USER)
        context.currentRoles.add(UserRole.Role.RESEARCHER)

        val error = wrapper.sharePrivateAppointment(
                request = SharePrivateAppointment.Request(
                        userId = user.id,
                        appointmentId = appointment.id
                )
        ){
            assertNull(it.success)
            assertNotNull(it.error)
        }

        // Make sure it was a failure and the correct reason
        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }
}