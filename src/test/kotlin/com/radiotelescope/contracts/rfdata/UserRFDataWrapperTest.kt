package com.radiotelescope.contracts.rfdata

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedAppointmentData.sql"])
internal class UserRFDataWrapperTest {
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
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var rfDataRepo: IRFDataRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private val context = FakeUserContext()
    private lateinit var factory: RFDataFactory
    private lateinit var wrapper: UserRFDataWrapper

    private lateinit var completedAppointment: Appointment
    private lateinit var differentUser: User
    private lateinit var adminUser: User
    private lateinit var ownerUser: User

    @Before
    fun setUp() {
        // Ensure the sql script was executed
        assertEquals(1, telescopeRepo.count())
        assertEquals(1, userRepo.count())
        assertEquals(1, appointmentRepo.count())
        assertEquals(10, rfDataRepo.count())

        // Grab the persisted user
        ownerUser = userRepo.findAll().first()

        // Persist two more users
        differentUser = testUtil.createUser("cspath617@gmail.com")
        adminUser = testUtil.createUser("admin@radiotele.com")

        factory = BaseRFDataFactory(
                appointmentRepo = appointmentRepo,
                rfDataRepo = rfDataRepo
        )

        wrapper = UserRFDataWrapper(
                context = context,
                factory = factory,
                appointmentRepo = appointmentRepo
        )

        // Grab the appointment and make sure it is public
        completedAppointment = appointmentRepo.findAll().first()
        completedAppointment.isPublic = true
        appointmentRepo.save(completedAppointment)
    }

    @Test
    fun testRetrieve_Public_UserIsOwner_Success() {
        // Simulate a login and add the necessary role
        context.login(ownerUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Call the factory method
        val error = wrapper.retrieveAppointmentData(
                appointmentId = completedAppointment.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_Public_UserNotOwner_Success() {
        // Simulate a login as a different user (not the admin)
        context.login(differentUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Call the factory method
        val error = wrapper.retrieveAppointmentData(
                appointmentId = completedAppointment.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_Private_Admin_Success() {
        // Simulate a login as a different user (not the admin)
        context.login(differentUser.id)
        context.currentRoles.addAll(listOf(UserRole.Role.USER, UserRole.Role.ADMIN))

        // Call the factory method
        val error = wrapper.retrieveAppointmentData(
                appointmentId = completedAppointment.id
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_Private_UserNotOwner_Failure() {
        // Make the appointment private
        completedAppointment.isPublic = false
        appointmentRepo.save(completedAppointment)

        // Simulate a login as a different user (not the admin)
        context.login(differentUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Call the factory method
        val error = wrapper.retrieveAppointmentData(
                appointmentId = completedAppointment.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRetrieve_NotLoggedIn_Failure() {
        // Call the factory method
        val error = wrapper.retrieveAppointmentData(
                appointmentId = completedAppointment.id
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testRetrieve_NonExistentAppointment_Failure() {
        // Call the factory method with an invalid id
        val error = wrapper.retrieveAppointmentData(
                appointmentId = 311L
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertNull(error!!.missingRoles)
        assertNotNull(error.invalidResourceId)
    }
}