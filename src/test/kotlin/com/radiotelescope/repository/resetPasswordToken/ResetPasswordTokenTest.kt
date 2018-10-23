package com.radiotelescope.repository.resetPasswordToken

import com.radiotelescope.TestUtil
import org.junit.Assert
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
internal class ResetPasswordTokenTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var resetPasswordTokenRepo: IResetPasswordTokenRepository

    private var token = ""

    @Before
    fun init(){
        val resetPasswordToken = testUtil.createResetPasswordToken(
                testUtil.createUser("rpim@ycp.edu")
        )

        token = resetPasswordToken.token
    }

    @Test
    fun testExistsByToken() {
        // Use the variable set in the set up method
        val exists: Boolean = resetPasswordTokenRepo.existsByToken(token)

        // The ResetPasswordToken Entity should exist
        Assert.assertTrue(exists)
    }

    @Test
    fun testFindByToken() {
        // Use the variable set in the set up method
        val resetPasswordToken = resetPasswordTokenRepo.findByToken(token)

        // The resetPasswordToken val should not be null
        Assert.assertNotNull(resetPasswordToken)
    }
}