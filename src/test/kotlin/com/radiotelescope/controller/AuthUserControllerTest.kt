package com.radiotelescope.controller

import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AuthenticatedUserToken
import com.radiotelescope.security.service.RetrieveAuthUserService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AuthUserControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository
    @Autowired
    private lateinit var userRepo: IUserRepository
    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private lateinit var retrieveAuthUserService: RetrieveAuthUserService

    private lateinit var authUserController: AuthUserController

    private lateinit var user: User

    @Before
    override fun init() {
        super.init()
        // Create a user
        user = testUtil.createUser("vmaresca@ycp.edu")

        retrieveAuthUserService = RetrieveAuthUserService(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )

        // initialize the authUserController
        authUserController = AuthUserController(
                retrieveAuthUserService,
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessfulAdminResponse(){
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

        SecurityContextHolder.getContext().authentication = authenticatedUserToken

        val result = authUserController.execute()

        assertNotNull(result)
        assertTrue(result.data != null)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created, log records are not implemented in userContext
        //assertEquals(1, logRepo.count())
    }

    @Test
    fun testSuccessfulGuestResponse(){
        // Give the user a role of GUEST
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a list of GrantedAuthority objects that matches the fake user to send to the authUserController
        // Because that is the object type that it is expecting
        val userAuthorities = arrayListOf<SimpleGrantedAuthority>()
        userAuthorities.add(SimpleGrantedAuthority("ROLE_USER"))
        userAuthorities.add(SimpleGrantedAuthority("ROLE_GUEST"))

        val authenticatedUserToken = AuthenticatedUserToken(
                email = user.email,
                password = user.password,
                authorities = userAuthorities,
                userId = user.id
        )

        SecurityContextHolder.getContext().authentication = authenticatedUserToken

        val result = authUserController.execute()

        assertNotNull(result)
        assertTrue(result.data != null)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created, log records are not implemented in userContext
        //assertEquals(1, logRepo.count())
    }

    @Test
    fun testFailedResponse(){
        // Execute the controller without 'logging in' a user through spring
        val result = authUserController.execute()

        assertNotNull(result)
        assertTrue(result.data == null)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created, log records are not implemented in userContext
        //assertEquals(1, logRepo.count())
    }
}