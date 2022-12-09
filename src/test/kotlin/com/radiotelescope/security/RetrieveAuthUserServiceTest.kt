package com.radiotelescope.security

import com.radiotelescope.security.service.RetrieveAuthUserService
import com.radiotelescope.repository.log.ILogRepository
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
import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.role.IUserRoleRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveAuthUserServiceTest : AbstractSpringTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository
    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository
    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var retrieveAuthUserService: RetrieveAuthUserService

    private lateinit var user: User

    @Before
    fun init(){
        // Create a user
        // Status is auto set to ACTIVE in the default create user function
        user = testUtil.createUser("vmaresca@ycp.edu")

        retrieveAuthUserService = RetrieveAuthUserService(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }

    @Test
    fun testSuccessfulExecute(){
        // Give the user a role of ADMIN
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Create a list of GrantedAuthority objects that matches the fake user to send to the authUserController
        // Because that is the object type that it is expecting
        val userAuthorities = arrayListOf<SimpleGrantedAuthority>()
        userAuthorities.add(SimpleGrantedAuthority("ROLE_USER"))
        userAuthorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))

        val authenticatedUserToken = AuthenticatedUserToken(
                email = user.email,
                password = user.password,
                authorities = userAuthorities,
                userId = user.id
        )

        // Login the user through spring
        SecurityContextHolder.getContext().authentication = authenticatedUserToken

        val result = retrieveAuthUserService.execute()

        assertNotNull(result)
        assertTrue(result.success is UserSession)
        assertNull(result.error)
    }

    @Test
    fun testFailedStatusValidation(){
        // Give the user a role of ADMIN
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )
        user.status = User.Status.BANNED

        // Create a list of GrantedAuthority objects that matches the fake user to send to the authUserController
        // Because that is the object type that it is expecting
        val userAuthorities = arrayListOf<SimpleGrantedAuthority>()
        userAuthorities.add(SimpleGrantedAuthority("ROLE_USER"))
        userAuthorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))

        val authenticatedUserToken = AuthenticatedUserToken(
                email = user.email,
                password = user.password,
                authorities = userAuthorities,
                userId = user.id
        )

        // Login the user through spring
        SecurityContextHolder.getContext().authentication = authenticatedUserToken

        val result = retrieveAuthUserService.execute()

        assertNotNull(result)
        assertNotNull(result.error)
        assertEquals("{STATUS=[Account is currently banned]}", result.error.toString())
        assertTrue(result.success !is UserSession)
    }

    @Test
    fun testGetAuthorities(){
        // Give the user a role of ADMIN
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        // Create a list of GrantedAuthority objects that matches the fake user to send to the authUserController
        // Because that is the object type that it is expecting
        val userAuthorities = arrayListOf<SimpleGrantedAuthority>()
        userAuthorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))

        val authenticatedUserToken = AuthenticatedUserToken(
                email = user.email,
                password = user.password,
                authorities = userAuthorities,
                userId = user.id
        )

        // Login the user through spring
        SecurityContextHolder.getContext().authentication = authenticatedUserToken

        val result = retrieveAuthUserService.execute()

        assertNotNull(result)
        assertTrue(result.success is UserSession)
        // Test the getAuthorities function to make sure it gets the same authorities we initially gave it, ie ROLE_ADMIN
        assertEquals(result.success?.roles, userAuthorities)
        assertNull(result.error)
    }
}