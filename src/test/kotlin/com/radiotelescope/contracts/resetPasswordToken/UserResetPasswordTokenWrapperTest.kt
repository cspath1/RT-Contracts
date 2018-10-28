package com.radiotelescope.contracts.resetPasswordToken

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
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
internal class UserResetPasswordTokenWrapperTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var resetPasswordTokeRepo: IResetPasswordTokenRepository

    private lateinit var wrapper: UserResetPasswordTokenWrapper
    private lateinit var user: User

    @Before
    fun init() {
        // Initialize the wrapper
        wrapper = UserResetPasswordTokenWrapper(resetPasswordTokeRepo, userRepo)

        // Persist user
        user = testUtil.createUser("rpim@ycp.edu")
    }

    @Test
    fun testValid_CreateResetPasswordToken_Success(){
        val (token, error) = wrapper.resetPasswordToken(
                email = user.email
        ).execute()

        assertNotNull(token)
        assertNull(error)
    }
}