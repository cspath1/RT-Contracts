package com.radiotelescope.contracts.appointment.update

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.User
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
internal class CoordinateAppointmentUpdateTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    private lateinit var appointment: Appointment
    private lateinit var user: User

    private var appointmentId = -1L
    private var userId = -1L

    private val twoHours = 2 * 60 * 60 * 1000

    private lateinit var baseRequest: CoordinateAppointmentUpdate.Request

    @Before
    fun setUp() {
        // Make sure the sql script was executed
        assertEquals(1, telescopeRepo.count())

        // Persist the user
        user = testUtil.createUser(
                email = "rpim@ycp.edu"
        )
        userId = user.id

        // Persist the appointment
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(System.currentTimeMillis() + 10000L),
                endTime = Date(System.currentTimeMillis() + 30000L),
                isPublic = true,
                status = Appointment.Status.SCHEDULED,
                telescopeId = 1L,
                type = Appointment.Type.POINT
        )
        appointmentId = appointment.id

        baseRequest = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                startTime = Date(appointment.endTime.time + twoHours),
                endTime = Date(appointment.endTime.time + (twoHours * 2)),
                telescopeId = 1L,
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )
    }

    @Test
    fun testValid_CorrectConstraints_Guest_Success(){
        // Make the user a guest
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure it was not error
        assertNotNull(id)
        assertNull(errors)

        // Make sure the information was updated correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertEquals(baseRequest.id, theAppointment.id)
        assertNull(appointment.celestialBody)
        assertNull(appointment.orientation)
        assertEquals(baseRequest.endTime, theAppointment.endTime)
        assertEquals(baseRequest.startTime, theAppointment.startTime)
        assertEquals(baseRequest.isPublic, theAppointment.isPublic)
        assertEquals(baseRequest.telescopeId, theAppointment.telescopeId)
        assertEquals(1, theAppointment.coordinateList.size)
    }

    @Test
    fun testValid_CorrectConstraints_Other_Success() {
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = baseRequest,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure it was not error
        assertNotNull(id)
        assertNull(errors)

        // Make sure the information was updated correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertEquals(baseRequest.id, theAppointment.id)
        assertNull(appointment.celestialBody)
        assertNull(appointment.orientation)
        assertEquals(baseRequest.endTime, theAppointment.endTime)
        assertEquals(baseRequest.startTime, theAppointment.startTime)
        assertEquals(baseRequest.isPublic, theAppointment.isPublic)
        assertEquals(baseRequest.telescopeId, theAppointment.telescopeId)
        assertEquals(1, theAppointment.coordinateList.size)
    }

    @Test
    fun testValidConstraints_ChangedType_Success() {
        // Make the user a researcher
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Create an appointment that is a different type
        val appointment = testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() +  50000L),
                endTime = Date(System.currentTimeMillis() + 100000L),
                isPublic = true,
                type = Appointment.Type.CELESTIAL_BODY
        )

        // Create a copy of the request with the appointment above
        val requestCopy = baseRequest.copy(
                id = appointment.id
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        assertNotNull(id)
        assertNull(errors)

        // Make sure the information was updated correctly
        val theAppointment = appointmentRepo.findById(id!!).get()

        assertNotEquals(baseRequest.id, theAppointment.id)
        assertNull(appointment.celestialBody)
        assertNull(appointment.orientation)
        assertEquals(baseRequest.endTime, theAppointment.endTime)
        assertEquals(baseRequest.startTime, theAppointment.startTime)
        assertEquals(baseRequest.isPublic, theAppointment.isPublic)
        assertEquals(baseRequest.telescopeId, theAppointment.telescopeId)
        assertEquals(1, theAppointment.coordinateList.size)
    }

    @Test
    fun testInvalid_AppointmentDoesNotExist_Failure() {
        // Create a copy of the request with an invalid id
        val requestCopy = baseRequest.copy(
                id = 311
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeGreaterThanEndTime_Failure() {
        // Create a copy of the request with a start time after the end time
        val requestCopy = baseRequest.copy(
                startTime = Date(appointment.endTime.time + 40000L),
                endTime = Date(appointment.endTime.time + 10000L)
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.END_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_StartTimeInPast_Failure() {
        // Create a copy of the request with a start time in the past
        val requestCopy = baseRequest.copy(
                startTime = Date(appointment.startTime.time - 40000L),
                endTime = Date(appointment.endTime.time + 10000L)
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.START_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_TelescopeDoesNotExist_Failure() {
        // Create a copy of the request with an invalid telescope
        val requestCopy = baseRequest.copy(
                telescopeId = 311L
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a copy of the request with an appointment time over the limit
        val requestCopy = baseRequest.copy(
                startTime = Date(appointment.endTime.time + twoHours),
                endTime = Date(appointment.endTime.time + (twoHours * 5))
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        // Create a copy of the request with an appointment time over the limit
        val requestCopy = baseRequest.copy(
                startTime = Date(appointment.endTime.time + twoHours),
                endTime = Date(appointment.endTime.time + (twoHours * 29))
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the expected reason
        assertTrue(errors!![ErrorTag.ALLOTTED_TIME].isNotEmpty())
    }

    @Test
    fun testInvalid_NoCategoryOfService_Failure() {
        // Make the user a researcher (unapproved)
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = false
        )

        // Create a copy of the request with an appointment time over the limit
        val requestCopy = baseRequest.copy(
                startTime = Date(appointment.endTime.time + twoHours),
                endTime = Date(appointment.endTime.time + (twoHours * 29))
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointment.id,
                telescopeId = 1L,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(startTime + 1000L),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime + 1000L),
                endTime = Date(endTime - 1000L),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(endTime - 1000L),
                endTime = Date(endTime),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime - 2000L),
                endTime = Date(startTime + 500L),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(endTime - 500L),
                endTime = Date(endTime + 1000L),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime - 1000L),
                endTime = Date(startTime),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(endTime),
                endTime = Date(endTime + 2000L),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.REQUESTED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )
        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.CANCELED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime + 1L),
                endTime = Date(endTime + 1L),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
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
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        appointment = testUtil.createAppointment(
                user = user,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                type = Appointment.Type.POINT
        )

        val conflict = CoordinateAppointmentUpdate.Request(
                id = appointmentId,
                telescopeId = 1L,
                startTime = Date(startTime),
                endTime = Date(endTime),
                isPublic = true,
                hours = 12,
                minutes = 12,
                seconds = 12,
                declination = 69.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = conflict,
                appointmentRepo = appointmentRepo,
                userRoleRepo = userRoleRepo,
                telescopeRepo = telescopeRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.OVERLAP].isNotEmpty())
    }

    @Test
    fun testInvalid_HoursTooLow_Failure() {
        // Create a copy of the request with hours below 0
        val requestCopy = baseRequest.copy(
                hours = -311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testInvalid_HoursTooHigh_Failure() {
        // Create a copy of the request with hours above 24
        val requestCopy = baseRequest.copy(
                hours = 311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testInvalid_MinutesTooLow_Failure() {
        // Create a copy of the request with minutes below 0
        val requestCopy = baseRequest.copy(
                minutes = -311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testInvalid_MinutesTooHigh_Failure() {
        // Create a copy of the request with minutes above 60
        val requestCopy = baseRequest.copy(
                minutes = 311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testInvalid_SecondsTooLow_Failure() {
        // Create a copy of the request with seconds below 0
        val requestCopy = baseRequest.copy(
                seconds = -311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testInvalid_SecondsTooHigh_Failure() {
        // Create a copy of the request with seconds above 60
        val requestCopy = baseRequest.copy(
                seconds = 311
        )

        // Execute the command
        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testInvalid_DeclinationTooLow_Failure() {
        // Create a copy of the request with a declination below 0
        val requestCopy = baseRequest.copy(
                declination = -99.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure it was for the expected reason
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testInvalid_DeclinationTooGreat_Failure() {
        // Create a copy of the request with a declination below 0
        val requestCopy = baseRequest.copy(
                declination = 99.0
        )

        val (id, errors) = CoordinateAppointmentUpdate(
                request = requestCopy,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo,
                userRoleRepo = userRoleRepo,
                coordinateRepo = coordinateRepo,
                orientationRepo = orientationRepo
        ).execute()

        // Make sure it was for the expected reason
        assertNotNull(errors)
        assertNull(id)

        // Make sure it was for the expected reason
        assertTrue(errors!![ErrorTag.DECLINATION].isNotEmpty())
    }
}