package com.radiotelescope.contracts.updateEmailToken

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.updateEmailToken.UpdateEmailToken
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.FakeUserContext
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
internal class UserUpdateEmailTokenWrapperTest {
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
    private var otherUserId = -1L

    private val context = FakeUserContext()
    private lateinit var factory: BaseUpdateEmailTokenFactory
    private lateinit var wrapper: UserUpdateEmailTokenWrapper

    private lateinit var token: UpdateEmailToken

    @Before
    fun init(){
        // Initialize the factory and wrapper
        factory = BaseUpdateEmailTokenFactory(
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        )

        wrapper = UserUpdateEmailTokenWrapper(
                context = context,
                factory = factory,
                userRepo = userRepo
        )

        // Persist the users
        user = testUtil.createUser("rpim@ycp.edu")
        otherUser = testUtil.createUser("rpim1@ycp.edu")

        // Set the user Id
        userId = user.id
        otherUserId = otherUser.id

        // Persist the token
        token = testUtil.createUpdateEmailToken(
                email = "rpim2@ycp.edu",
                token = "someToken",
                user = user
        )
    }

    @Test
    fun testValid_RequestUpdateEmail_Owner_Success(){
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.requestUpdateEmail(
                CreateUpdateEmailToken.Request(
                        userId = userId,
                        email = "rpim1234@ycp.edu",
                        emailConfirm = "rpim1234@ycp.edu"
                )
        ){
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testInvalid_RequestUpdateEmail_NotOwner_Failure(){
        // Simulate a login
        context.login(otherUserId)
        context.currentRoles.add(UserRole.Role.USER)

        val error = wrapper.requestUpdateEmail(
                CreateUpdateEmailToken.Request(
                        userId = userId,
                        email = "rpim1234@ycp.edu",
                        emailConfirm = "rpim1234@ycp.edu"
                )
        ){
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        Assert.assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testInvalid_RequestUpdateEmail_NotLoggedIn_Failure(){
        // Simulate a login

        val error = wrapper.requestUpdateEmail(
                CreateUpdateEmailToken.Request(
                        userId = userId,
                        email = "rpim1234@ycp.edu",
                        emailConfirm = "rpim1234@ycp.edu"
                )
        ){
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
        Assert.assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testValid_UpdateEmail_Success(){
        val (id, error) = wrapper.updateEmail(
                token = token.token
        ).execute()

        assertNotNull(id)
        assertNull(error)
    }

}