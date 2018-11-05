package com.radiotelescope.repository.updateEmailToken

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
internal class UpdateEmailTokenTest {
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
    private lateinit var updateEmailTokenRepo: IUpdateEmailTokenRepository

    private var token = "someToken"

    @Before
    fun init() {
        testUtil.createUpdateEmailToken(
                user = testUtil.createUser("rpim@ycp.edu"),
                token = token
        )
    }

    @Test
    fun testExistsByToken() {
        val exists = updateEmailTokenRepo.existsByToken(token)

        Assert.assertTrue(exists)
    }

    @Test
    fun testFindByToken() {
        val theAccountActivateToken = updateEmailTokenRepo.findByToken(token)

        Assert.assertNotNull(theAccountActivateToken)
    }

}