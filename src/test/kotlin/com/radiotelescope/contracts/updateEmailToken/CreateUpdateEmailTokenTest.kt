package com.radiotelescope.contracts.updateEmailToken

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert
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
internal class CreateUpdateEmailTokenTest {
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
    private lateinit var updateEmailTokenRepo: IUpdateEmailTokenRepository

    private lateinit var user: User
    private lateinit var otherUser: User
    private var userId = -1L

    @Before
    fun setUp() {
        user = testUtil.createUser("rpim@ycp.edu")
        otherUser = testUtil.createUser("otherUser@ycp.edu")

        userId = user.id
    }

    @Test
    fun testValid_CorrectConstraints_Success(){
        val (token, errors) = CreateUpdateEmailToken(
                request = CreateUpdateEmailToken.Request(
                        userId = userId,
                        email = "rpim123@ycp.edu",
                        emailConfirm = "rpim123@ycp.edu"
                ),
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Make sure the command was a success
        Assert.assertNotNull(token)
        Assert.assertNull(errors)
    }

    @Test
    fun testInvalid_BlankEmail_Failure(){
        val (token, errors) = CreateUpdateEmailToken(
                request = CreateUpdateEmailToken.Request(
                        userId = user.id,
                        email = "",
                        emailConfirm = ""
                ),
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Make sure the command was a failure
        Assert.assertNull(token)
        Assert.assertNotNull(errors)

        // Ensure it failed correctly
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testInvalid_NotEmail_Failure(){
        val (token, errors) = CreateUpdateEmailToken(
                request = CreateUpdateEmailToken.Request(
                        userId = user.id,
                        email = "not an email",
                        emailConfirm = "not an email"
                ),
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Make sure the command was a failure
        Assert.assertNull(token)
        Assert.assertNotNull(errors)

        // Ensure it failed correctly
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testInvalid_EmailInUse_Failure(){
        val (token, errors) = CreateUpdateEmailToken(
                request = CreateUpdateEmailToken.Request(
                        userId = user.id,
                        email = otherUser.email,
                        emailConfirm = otherUser.email
                ),
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Make sure the command was a failure
        Assert.assertNull(token)
        Assert.assertNotNull(errors)

        // Ensure it failed correctly
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testInvalid_EmailDoesNotMatch_Failure(){
        val (token, errors) = CreateUpdateEmailToken(
                request = CreateUpdateEmailToken.Request(
                        userId = user.id,
                        email = "rpim123@ycp.edu",
                        emailConfirm = "rpim123456789@ycp.edu"
                ),
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Make sure the command was a failure
        Assert.assertNull(token)
        Assert.assertNotNull(errors)

        // Ensure it failed correctly
        assertTrue(errors!![ErrorTag.EMAIL_CONFIRM].isNotEmpty())
    }

    @Test
    fun testInvalid_SameEmail_Failure(){
        val (token, errors) = CreateUpdateEmailToken(
                request = CreateUpdateEmailToken.Request(
                        userId = user.id,
                        email = user.email,
                        emailConfirm = user.email
                ),
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Make sure the command was a failure
        Assert.assertNull(token)
        Assert.assertNotNull(errors)

        // Ensure it failed correctly
        assertTrue(errors!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testInvalid_InvalidUserId_Failure() {
        val (token, errors) = CreateUpdateEmailToken(
                request = CreateUpdateEmailToken.Request(
                        userId = 311L,
                        email = "cspath1@ycp.edu",
                        emailConfirm = "cspath1@ycp.edu"
                ),
                userRepo = userRepo,
                updateEmailTokenRepo = updateEmailTokenRepo
        ).execute()

        // Make sure the command was a failure
        assertNull(token)
        assertNotNull(errors)

        // Ensure it failed for the expected reason
        assertTrue(errors!![ErrorTag.USER_ID].isNotEmpty())
    }
}