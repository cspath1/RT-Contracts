package com.radiotelescope.contracts.role

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ValidateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private val baseValidateRequest = Validate.Request(
            id = -1L,
            role = UserRole.Role.MEMBER
    )

    var unapprovedId: Long = -1L

    private var userId = -1L

    @Before
    fun setUp() {
        // Create a user and a role that needs approval
        val user = testUtil.createUser("cspath1@ycp.edu")
        userId = user.id

        val roles = testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.MEMBER,
                isApproved = false
        )

        roles.forEach {
            if (!it.approved)
                unapprovedId = it.id
        }
    }

    @Test
    fun testAllottedTimeCap_Set_Guest(){
        val requestCopy = baseValidateRequest.copy(
                role = UserRole.Role.GUEST,
                id = unapprovedId
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(theResponse)
        assertNull(errors)

        assertEquals(Appointment.GUEST_APPOINTMENT_TIME_CAP, allottedTimeCapRepo.findByUserId(userId).allottedTime)
    }

    @Test
    fun testAllottedTimeCap_Set_Student(){
        val requestCopy = baseValidateRequest.copy(
                role = UserRole.Role.STUDENT,
                id = unapprovedId
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(theResponse)
        assertNull(errors)

        assertEquals(Appointment.STUDENT_APPOINTMENT_TIME_CAP, allottedTimeCapRepo.findByUserId(userId).allottedTime)
    }

    @Test
    fun testAllottedTimeCap_Set_Member(){
        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(theResponse)
        assertNull(errors)

        assertEquals(Appointment.MEMBER_APPOINTMENT_TIME_CAP, allottedTimeCapRepo.findByUserId(userId).allottedTime)
    }

    @Test
    fun testAllottedTimeCap_Set_Researcher(){
        val requestCopy = baseValidateRequest.copy(
                role = UserRole.Role.RESEARCHER,
                id = unapprovedId
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(theResponse)
        assertNull(errors)

        assertNull(allottedTimeCapRepo.findByUserId(userId).allottedTime)
    }

    @Test
    fun testValidConstraints_SameRole_Success() {
        // Keep the role the same
        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(theResponse)
        assertNull(errors)
        assertEquals(unapprovedId, theResponse!!.id)

        val theRole = userRoleRepo.findById(theResponse.id).get()
        assertEquals(requestCopy.role, theRole.role)

        val allottedTimeCap = allottedTimeCapRepo.findByUserId(userId)
        assertEquals(Appointment.MEMBER_APPOINTMENT_TIME_CAP, allottedTimeCap.allottedTime)
    }

    @Test
    fun testValidConstraint_DifferentRole_Success() {
        // Set the role to something different than
        // the originally persisted value
        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId,
                role = UserRole.Role.GUEST
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(theResponse)
        assertNull(errors)
        assertEquals(unapprovedId, theResponse!!.id)

        val theRole = userRoleRepo.findById(theResponse.id).get()
        assertEquals(requestCopy.role, theRole.role)
        assertNotEquals(baseValidateRequest.role, theRole.role)

        val allottedTimeCap = allottedTimeCapRepo.findByUserId(userId)
        assertEquals(Appointment.GUEST_APPOINTMENT_TIME_CAP, allottedTimeCap.allottedTime)
    }

    @Test
    fun testInvalidId_Failure() {
        // Non-existent id
        val requestCopy = baseValidateRequest.copy(
                id = 311L
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNull(theResponse)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testRoleAlreadyApproved_Failure() {
        val theRole = userRoleRepo.findById(unapprovedId).get()
        theRole.approved = true
        userRoleRepo.save(theRole)

        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNull(theResponse)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.APPROVED].isNotEmpty())
    }

    @Test
    fun testRoleAdmin_Failure() {
        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId,
                role = UserRole.Role.ADMIN
        )

        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNull(theResponse)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.ROLE].isNotEmpty())
    }

    @Test
    fun testValid_RemoveOldRole_Success() {
        testUtil.createUserRoleForUser(
                user = userRepo.findById(userId).get(),
                role = UserRole.Role.STUDENT,
                isApproved = false
        )
        testUtil.createUserRoleForUser(
                user = userRepo.findById(userId).get(),
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId,
                role = UserRole.Role.MEMBER
        )
        val (theResponse, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(theResponse)
        assertNull(errors)

        val theRoles = userRoleRepo.findAllByUserId(userId)

        // Make sure all other role were removed
        assertEquals(2, theRoles.size)

        // Make sure the roles are as expected
        theRoles.forEach {
            if (it.id == theResponse!!.id) {
                assertEquals(UserRole.Role.MEMBER, it.role)
            } else {
                assertEquals(UserRole.Role.USER, it.role)
            }
        }

        val allottedTimeCap = allottedTimeCapRepo.findByUserId(userId)
        assertEquals(Appointment.MEMBER_APPOINTMENT_TIME_CAP, allottedTimeCap.allottedTime)
    }
}