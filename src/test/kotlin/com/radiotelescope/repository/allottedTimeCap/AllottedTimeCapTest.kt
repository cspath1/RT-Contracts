package com.radiotelescope.repository.allottedTimeCap

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AllottedTimeCapTest {
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
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var testUtil: TestUtil

    private lateinit var user: User

    private lateinit var allottedTimeCap: AllottedTimeCap

    private var allottedTime: Long? = 424242

    @Before
    fun setup(){
        user = testUtil.createUser("lferree@ycp.edu")

        allottedTimeCap = testUtil.createAllottedTimeCapForUser(user, allottedTime)
    }

    @Test
    fun testFindByUserId(){
        val timeCap = allottedTimeCapRepo.findByUserId(user.id)

        assertEquals(user, timeCap.user)
        assertEquals(allottedTime, timeCap.allottedTime)
    }
}