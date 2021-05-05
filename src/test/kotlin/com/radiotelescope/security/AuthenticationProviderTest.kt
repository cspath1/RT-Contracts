package com.radiotelescope.security

import com.radiotelescope.security.service.RetrieveAuthUserService
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
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.security.service.UserDetailsServiceImpl
import com.radiotelescope.services.ses.MockAwsSesSendService
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AuthenticationProviderTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository
    @Autowired
    private lateinit var userRepo: IUserRepository
    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository
    @Autowired
    private lateinit var loginAttemptRepo: ILoginAttemptRepository

    private lateinit var awsSesSendService: MockAwsSesSendService

    private lateinit var retrieveAuthUserService: RetrieveAuthUserService

    private lateinit var user: User

    private lateinit var authenticationProvider: AuthenticationProviderImpl

    private lateinit var userDetailsService: UserDetailsServiceImpl

    @Before
    fun init(){
        // Create a user
        // Status is auto set to ACTIVE in the default create user function
        user = testUtil.createUser("vmaresca@ycp.edu")

        userDetailsService = UserDetailsServiceImpl(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )

        awsSesSendService = MockAwsSesSendService(true)

        authenticationProvider = AuthenticationProviderImpl(
                userDetailsService = userDetailsService,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo,
                awsSesSendService = awsSesSendService
        )
    }

    @Test
    fun testSuccessfulAuthenticate(){
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

        val result = authenticationProvider.authenticate(authenticatedUserToken)

        assertNotNull(result)
        assertTrue(result is AuthenticatedUserToken)
    }
}