package com.radiotelescope.repository.resetPasswordToken

import com.radiotelescope.TestUtil
import liquibase.integration.spring.SpringLiquibase
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

    @Test
    fun testFindAllByUserId() {
        //Persist user and resetPasswordToken
        val user = testUtil.createUser("rpim1@ycp.edu")
        testUtil.createResetPasswordToken(user)
        testUtil.createResetPasswordToken(user)
        testUtil.createResetPasswordToken(user)

        // Call method
        val list = resetPasswordTokenRepo.findAllByUserId(user.id)

        // Make sure the correct amount return
        Assert.assertEquals(3, list.size)

    }
}