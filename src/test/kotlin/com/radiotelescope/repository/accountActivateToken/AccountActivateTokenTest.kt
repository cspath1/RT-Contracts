package com.radiotelescope.repository.accountActivateToken

import com.radiotelescope.TestUtil
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
internal class AccountActivateTokenTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    private var token = "thebesttoken"

    @Before
    fun init() {
        testUtil.createAccountActivateToken(
                user = testUtil.createUser("cspath1@ycp.edu"),
                token = token
        )
    }

    @Test
    fun testExistsByToken() {
        val exists = accountActivateTokenRepo.existsByToken(token)

        assertTrue(exists)
    }

    @Test
    fun testFindByToken() {
        val theAccountActivateToken = accountActivateTokenRepo.findByToken(token)

        assertNotNull(theAccountActivateToken)
    }
}