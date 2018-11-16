package com.radiotelescope.contracts.appointment

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class UpdateTest {
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
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository


    private lateinit var appointment: Appointment
    private lateinit var user: User

    private var appointmentId = -1L
    private var userId = -1L

    private val twoHours = 2 * 60 * 60 * 1000

    @Before
    fun setUp() {
        // Make sure the sql script was executed
        assertEquals(1, telescopeRepo.count())

        // Persist the user
        user = testUtil.createUser(
                email = "rpim@ycp.edu",
                accountHash = "Test Account"
        )
        userId = user.id

        // Persist the appointment
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                status = Appointment.Status.SCHEDULED,
                telescopeId = 1L
        )
        appointmentId = appointment.id
    }

    @Test
    fun testValid_CorrectConstraints_Guest_Success(){
        // Make the user a guest
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + twoHours),
                        endTime = Date(appointment.endTime.time + (twoHours * 2)),
                        telescopeId = 1L,
                        isPublic = false,
                        rightAscension = 311.0,
                        declination = 69.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo

        ).execute()

        // Make sure it was not error
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testValid_CorrectConstraints_Other_Success() {
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + twoHours),
                        endTime = Date(appointment.endTime.time + (twoHours * 25)),
                        telescopeId = 1L,
                        isPublic = false,
                        rightAscension = 311.0,
                        declination = 69.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo

        ).execute()

        // Make sure it was not error
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testInvalid_AppointmentDoesNotExist_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = 123456789,
                        startTime = Date(appointment.endTime.time + 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = 1L,
                        isPublic = appointment.isPublic,
                        rightAscension = 311.0,
                        declination = 69.0

                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeGreaterThanEndTime_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + 40000L),
                        endTime = Date(appointment.endTime.time + 10000L),
                        telescopeId = 1L,
                        isPublic = appointment.isPublic,
                        rightAscension = 311.0,
                        declination = 69.0

                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeInPast_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(System.currentTimeMillis() - 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = 1L,
                        isPublic = appointment.isPublic,
                        rightAscension = 311.0,
                        declination = 69.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_TelescopeDoesNotExist_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = 123456789,
                        isPublic = appointment.isPublic,
                        rightAscension = 311.0,
                        declination = 69.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.TELESCOPE_ID].isNotEmpty())
    }

    @Test
    fun testInvalid_ExceedAllottedGuestLimit_Failure() {
        // Make the user a guest
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + twoHours),
                        endTime = Date(appointment.endTime.time + (twoHours * 5)),
                        telescopeId = 1L,
                        isPublic = false,
                        rightAscension = 311.0,
                        declination = 69.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo

        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.ALLOTTED_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_ExceededAllottedOtherLimit_Failure() {
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + twoHours),
                        endTime = Date(appointment.endTime.time + (twoHours * 29)),
                        telescopeId = 1L,
                        isPublic = false,
                        rightAscension = 311.0,
                        declination = 69.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo

        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.ALLOTTED_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_NoCategoryOfService_Failure() {
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.RESEARCHER,
                isApproved = false
        )

        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(appointment.endTime.time + twoHours),
                        endTime = Date(appointment.endTime.time + (twoHours * 29)),
                        telescopeId = 1L,
                        isPublic = false,
                        rightAscension = 311.0,
                        declination = 69.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo

        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.CATEGORY_OF_SERVICE].isNotEmpty())
    }

    @Test
    fun testValidSC_SameAppointment_Success(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointment.id,
                telescopeId = 1L,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testInvalidSC_StartAtStart_EndBeforeEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(startTime + 1000L),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalidSC_BetweenEndAndStart_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime + 1000L),
                endTime = Date(endTime - 1000L),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalidSC_StartAfterStart_EndAtEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(endTime - 1000L),
                endTime = Date(endTime),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalidSC_StartBeforeStart_EndBeforeEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime - 2000L),
                endTime = Date(startTime + 500L),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalidSC_StartBeforeEnd_EndAfterEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(endTime - 500L),
                endTime = Date(endTime + 1000L),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalidSC_EndAtStart_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime - 1000L),
                endTime = Date(startTime),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalidSC_StartAtEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(endTime),
                endTime = Date(endTime + 2000L),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalidSC_StartAtStart_EndAtEnd_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testValidSC_Requested_Success(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testValidSC_Canceled_Success(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L
        testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.CANCELED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testInvalidSC_OneConflictAndOneIsAppointment_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalidSC_NoneConflicts_Failure(){
        val startTime = System.currentTimeMillis() + 500000L
        val endTime = System.currentTimeMillis() +   900000L

        testUtil.createAppointment(
                user = user,
                startTime = Date(endTime + 2222L),
                endTime = Date(endTime + 4444L),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED
        )

        val conflict = Update.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                rightAscension = 311.0,
                declination = 69.0
        )

        val (id, errors) = Update(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }


    @Test
    fun testInvalid_RightAscensionTooLow_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(System.currentTimeMillis() - 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = 1L,
                        isPublic = appointment.isPublic,
                        rightAscension = -311.0,
                        declination = 69.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.RIGHT_ASCENSION].isNotEmpty())
    }

    @Test
    fun testInvalid_RightAscensionTooGreat_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(System.currentTimeMillis() - 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = 1L,
                        isPublic = appointment.isPublic,
                        rightAscension = 420.0,
                        declination = 19.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.RIGHT_ASCENSION].isNotEmpty())
    }

    @Test
    fun testInvalid_DeclinationTooLow_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(System.currentTimeMillis() - 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = 1L,
                        isPublic = appointment.isPublic,
                        rightAscension = 311.0,
                        declination = -99.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was for the expected reason
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testInvalid_DeclinationTooGreat_Failure() {
        val (id, errors) = Update(
                request = Update.Request(
                        id = appointmentId,
                        startTime = Date(System.currentTimeMillis() - 10000L),
                        endTime = Date(appointment.endTime.time + 40000L),
                        telescopeId = 1L,
                        isPublic = appointment.isPublic,
                        rightAscension = 311.0,
                        declination = 99.0
                ),
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure it was for the expected reason
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }
}