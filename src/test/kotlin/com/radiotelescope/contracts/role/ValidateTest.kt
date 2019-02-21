package com.radiotelescope.contracts.role

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
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
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class ValidateTest {
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
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val baseValidateRequest = Validate.Request(
            id = -1L,
            role = UserRole.Role.MEMBER
    )

    var unapprovedId: Long = -1L

    private var userId = -1L

    @Before
    fun setUp() {
        // Create a user and a role that needs approval
        val user = testUtil.createUser(
                email = "cspath1@ycp.edu",
                accountHash = "Test Account 1"
        )
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
        val (id, errors) = Validate(
                request = requestCopy,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        val theRoles = userRoleRepo.findAllByUserId(userId)

        // Make sure all other role were removed
        assertEquals(2, theRoles.size)

        // Make sure the roles are as expected
        theRoles.forEach {
            if (it.id == id) {
                assertEquals(UserRole.Role.MEMBER, it.role)
            } else {
                assertEquals(UserRole.Role.USER, it.role)
            }
        }

    }
}