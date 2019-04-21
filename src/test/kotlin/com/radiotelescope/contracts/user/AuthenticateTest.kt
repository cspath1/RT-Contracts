package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.loginAttempt.LoginAttempt
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AuthenticateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var loginAttemptRepo: ILoginAttemptRepository

    private val baseRequest = Authenticate.Request(
            email = "cspath1@ycp.edu",
            password = "Password"
    )

    private lateinit var user: User

    @Before
    fun setUp() {
        // Persist the User with the hashed password
        user = testUtil.createUserWithEncodedPassword(
                email = "cspath1@ycp.edu",
                password = "Password"
        )
        // Create a 0 timecap for the user
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 0L
        )

        // Make sure this was correctly executed
        assertEquals(1, userRepo.count())
        assertNotNull(user)
    }

    @Test
    fun testValidConstraints_Success() {
        // Create a failed login attempt
        val loginAttempt = LoginAttempt(loginTime = Date())
        loginAttempt.user = user
        loginAttemptRepo.save(loginAttempt)

        // Execute the command
        val (info, errors) = Authenticate(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // The info class should not be null
        assertNotNull(info)

        // The errors should be null
        assertNull(errors)

        // The failed login attempt should be deleted
        assertEquals(0, loginAttemptRepo.count())
        assertEquals(1, userRepo.count())
    }

    @Test
    fun testBlankEmail_Failure() {
        // Create a copy of the request with a blank email
        val requestCopy = baseRequest.copy(
                email = ""
        )

        // Execute the command
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // The errors should not be null
        assertNotNull(errors)

        // The data class should be
        assertNull(info)
    }

    @Test
    fun testBlankPassword_Failure() {
        // Create a copy of the request with a blank password
        val requestCopy = baseRequest.copy(
                password = ""
        )

        // Execute the command
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // The errors should not be null
        assertNotNull(errors)

        // The data class should be
        assertNull(info)
    }

    @Test
    fun testNonExistentEmail_Failure() {
        // Create a copy of the request with a non-existent email
        val requestCopy = baseRequest.copy(
                email = "spathcody@gmail.com"
        )

        // Execute the command
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // The errors should not be null
        assertNotNull(errors)

        // The data class should be
        assertNull(info)
    }

    @Test
    fun testPasswordsDoNotMatch_Failure() {
        // Create a copy of the request with a bad password
        val requestCopy = baseRequest.copy(
                password = "Passwrod"
        )

        // Execute the command
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // The errors should not be null
        assertNotNull(errors)

        // The data class should be
        assertNull(info)
    }

    @Test
    fun testFailedToLoginTwice_Failure() {
        // Create a copy of the request with a bad password
        val requestCopy = baseRequest.copy(
                password = "Passwrod"
        )

        // Execute the command
        Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // Execute the command for a second time
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // The errors should not be null
        assertNull(info)
        assertNotNull(errors)

        // Make sure there are 2 failed login attempts
        val theUserId = userRepo.findByEmail(requestCopy.email)!!.id
        assertEquals(2, loginAttemptRepo.findByUserId(theUserId).size)
    }

    @Test
    fun testFailedFirst_SucceededSecond_Success() {
        // Create a copy of the request with a bad password
        val requestCopy = baseRequest.copy(
                password = "Passwrod"
        )

        // Execute the command (fail)
        Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // Execute the command for a second time (succeed)
        val (info, errors) = Authenticate(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // The errors should be null
        assertNotNull(info)
        assertNull(errors)

        // Make sure there are 2 failed login attempts
        val theUserId = userRepo.findByEmail(requestCopy.email)!!.id
        assertEquals(0, loginAttemptRepo.findByUserId(theUserId).size)
    }

    @Test
    fun testAccountLocked_Failure() {
        // Create a copy of the request with a bad password
        val requestCopy = baseRequest.copy(
                password = "Passwrod"
        )

        lateinit var result:  SimpleResult<UserInfo, Multimap<ErrorTag, String>>
        for(i in 0 until 6) {
            // Execute the command for 6 times
            result = Authenticate(
                    request = requestCopy,
                    userRepo = userRepo,
                    userRoleRepo = userRoleRepo,
                    allottedTimeCapRepo = allottedTimeCapRepo,
                    loginAttemptRepo = loginAttemptRepo
            ).execute()
        }

        // The errors should be null
        assertNotNull(result.error)
        assertNull(result.success)

        // Make sure there are 6 failed login attempts
        val theUserId = userRepo.findByEmail(requestCopy.email)!!.id
        assertEquals(6, loginAttemptRepo.findByUserId(theUserId).size)
        assertTrue(result.error!![ErrorTag.LOGIN_ATTEMPT].isNotEmpty())
    }
}