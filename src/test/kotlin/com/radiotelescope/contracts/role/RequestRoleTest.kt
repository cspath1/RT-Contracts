package com.radiotelescope.contracts.role

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RequestRoleTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private lateinit var user: User
    private lateinit var baseRole: UserRole

    @Before
    fun setUp() {
        // Persist a user
        user = testUtil.createUser("rpim@ycp.edu")

        baseRole = testUtil.createUserRoleForUser(
                user,
                UserRole.Role.USER,
                true
        )
    }

    @Test
    fun testValid_CorrectConstraints_Success() {
        val(id, errors) = RequestRole(
                request = RequestRole.Request(
                        role = UserRole.Role.GUEST,
                        userId = user.id
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)
    }

    @Test
    fun testValid_OlderRequestIsRemove_Success() {
        // Make a request for Student user role
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.STUDENT,
                isApproved = false
        )

        // Simulate requesting a new role before the other has been
        // approved. In this case, the other unapproved role should
        // be deleted
        val(id, errors) = RequestRole(
                request = RequestRole.Request(
                        role = UserRole.Role.GUEST,
                        userId = user.id
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        val theRoles = userRoleRepo.findAllByUserId(user.id)

        // Make sure all requested role were removed
        assertEquals(2, theRoles.size)

        // Make sure all roles are as expected
        theRoles.forEach {
            when {
                it.id == id -> assertEquals(UserRole.Role.GUEST, it.role)
                baseRole.id == it.id -> assertEquals(baseRole.role, it.role)
            }
        }
    }

    @Test
    fun testInvalid_SameRole_Failure() {
        // Persist a User Role
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )
        val(id, errors) = RequestRole(
                request = RequestRole.Request(
                        role = UserRole.Role.RESEARCHER,
                        userId = user.id
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it fail because of the correct reason
        assertTrue(errors!![ErrorTag.ROLE].isNotEmpty())
    }

    @Test
    fun testInvalid_RequestAdminRole_Failure() {
        val(id, errors) = RequestRole(
                request = RequestRole.Request(
                        role = UserRole.Role.ADMIN,
                        userId = user.id
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it fail because of the correct reason
        assertTrue(errors!![ErrorTag.ROLE].isNotEmpty())
    }

    @Test
    fun testInvalid_RequestUserRole_Failure() {
        val(id, errors) = RequestRole(
                request = RequestRole.Request(
                        role = UserRole.Role.USER,
                        userId = user.id
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Make sure the command was a success
        assertNull(id)
        assertNotNull(errors)

        // Make sure it fail because of the correct reason
        assertTrue(errors!![ErrorTag.ROLE].isNotEmpty())
    }
}