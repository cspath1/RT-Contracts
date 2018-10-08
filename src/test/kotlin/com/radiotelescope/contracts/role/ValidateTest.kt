package com.radiotelescope.contracts.role

import com.radiotelescope.BaseDataJpaTest
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class ValidateTest : BaseDataJpaTest() {
    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val baseValidateRequest = Validate.Request(
            id = -1L,
            role = UserRole.Role.MEMBER
    )

    var unapprovedId: Long = -1L

    @Before
    fun setUp() {
        // Create a user and a role that needs approval
        val user = testUtil.createUser("cspath1@ycp.edu")

        val roles = testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.MEMBER,
                isApproved = false
        )

        roles.forEach {
            if (!it.approved)
                unapprovedId = it.id
        }
    }

    @Test
    fun testValidConstraints_SameRole_Success() {
        // Keep the role the same
        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId
        )

        val (id, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo
        ).execute()

        assertNotNull(id)
        assertNull(errors)
        assertEquals(unapprovedId, id)

        val theRole = userRoleRepo.findById(id!!).get()
        assertEquals(requestCopy.role, theRole.role)
    }

    @Test
    fun testValidConstraint_DifferentRole_Success() {
        // Set the role to something different than
        // the originally persisted value
        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId,
                role = UserRole.Role.GUEST
        )

        val (id, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo
        ).execute()

        assertNotNull(id)
        assertNull(errors)
        assertEquals(unapprovedId, id)

        val theRole = userRoleRepo.findById(id!!).get()
        assertEquals(requestCopy.role, theRole.role)
        assertNotEquals(baseValidateRequest.role, theRole.role)
    }

    @Test
    fun testInvalidId_Failure() {
        // Non-existent id
        val requestCopy = baseValidateRequest.copy(
                id = 311L
        )

        val (id, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo
        ).execute()

        assertNull(id)
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

        val (id, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo
        ).execute()

        assertNull(id)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.APPROVED].isNotEmpty())
    }

    @Test
    fun testRoleAdmin_Failure() {
        val requestCopy = baseValidateRequest.copy(
                id = unapprovedId,
                role = UserRole.Role.ADMIN
        )

        val (id, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo
        ).execute()

        assertNull(id)
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.ROLE].isNotEmpty())
    }
}