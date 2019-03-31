package com.radiotelescope.contracts.rfdata

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.security.FakeUserContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedAppointmentData.sql"])
internal class UserRFDataWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var rfDataRepo: IRFDataRepository

    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

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
        assertEquals(1, radioTelescopeRepo.count())
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
                appointmentRepo = appointmentRepo,
                viewerRepo = viewerRepo
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

    @Test
    fun testRetrieve_SharedWith_Success() {
        // Share with a user
        val sharedUser = testUtil.createUser("rpim2@ycp.edu")
        testUtil.createViewer(sharedUser, completedAppointment)
        completedAppointment.isPublic = false

        // Simulate a login and add the necessary role
        context.login(sharedUser.id)
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
    fun testRetrieve_NotSharedWith_Failure() {
        // Don't share with a user
        val notSharedUser = testUtil.createUser("rpim2@ycp.edu")
        completedAppointment.isPublic = false

        // Simulate a login and add the necessary role
        context.login(notSharedUser.id)
        context.currentRoles.add(UserRole.Role.USER)

        // Call the factory method
        val error = wrapper.retrieveAppointmentData(
                appointmentId = completedAppointment.id
        ) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

}