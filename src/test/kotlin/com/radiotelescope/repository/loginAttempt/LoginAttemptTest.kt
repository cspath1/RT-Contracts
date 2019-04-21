package com.radiotelescope.repository.loginAttempt

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class LoginAttemptTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var loginAttemptRepo: ILoginAttemptRepository

    private lateinit var user: User
    private var loginAttempt1Id = -1L
    private var loginAttempt2Id = -1L

    @Before
    fun setUp() {
        // Persist users
        user = testUtil.createUser("rpim@ycp.edu")

        loginAttempt1Id = testUtil.createLoginAttempt(user).id
        loginAttempt2Id = testUtil.createLoginAttempt(user).id
    }

    @Test
    fun testFindByUserId() {
        val listOfLoginAttempt = loginAttemptRepo.findByUserId(user.id)

        assertEquals(2, listOfLoginAttempt.size)
        assertEquals(loginAttempt1Id, listOfLoginAttempt[0].id)
        assertEquals(loginAttempt2Id, listOfLoginAttempt[1].id)
    }
}