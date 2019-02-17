package com.radiotelescope.repository.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.UserRole
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert
import org.junit.Assert.assertTrue
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
internal class UserTest {
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

    private var email: String = ""

    @Before
    fun setUp() {
        // Instantiate and persist a User Entity Object
        val user = testUtil.createUser("cspath1@ycp.edu")
        val admin1 = testUtil.createUser("rpim@ycp.edu")
        testUtil.createUserRolesForUser(admin1, UserRole.Role.ADMIN, true)
        val admin2 = testUtil.createUser("rpim2@ycp.edu")
        testUtil.createUserRolesForUser(admin2, UserRole.Role.ADMIN, true)

        // Set the email variable to be used used in the IUserRepository existsByEmail query
        email = user.email
    }

    @Test
    fun testExistByEmail() {
        // Use the variable set in the set up method
        val exists: Boolean = userRepo.existsByEmail(email)

        // The User Entity should exist
        Assert.assertTrue(exists)
    }

    @Test
    fun testFindByEmail() {
        // Use the variable set in the set up method
        val user = userRepo.findByEmail(email)

        // The user val should not be null
        Assert.assertNotNull(user)
    }

    @Test
    fun testPasswordRegex() {
        // All lowercase, under 8
        Assert.assertFalse("password".matches(User.passwordRegex))

        // One uppercase, one lowercase, under 8
        Assert.assertFalse("Password".matches(User.passwordRegex))

        // One of each, under 8
        Assert.assertFalse("1qW#".matches(User.passwordRegex))

        // One uppercase, one lowercase, one digit, over 8
        Assert.assertTrue("Password1".matches(User.passwordRegex))

        // One uppercase, one lowercase, one digit, over 8
        Assert.assertTrue("GoodPassword1".matches(User.passwordRegex))

        // One uppercase, one lowercase, one special character, over 8
        Assert.assertTrue("GoodPassword?".matches(User.passwordRegex))

        // One lowercase, one special character, one digit, over 8
        Assert.assertTrue("goodpassword?1".matches(User.passwordRegex))

        // All four, over 8
        Assert.assertTrue("GoodPassword!?3".matches(User.passwordRegex))
    }

    @Test
    fun findAllAdminEmail(){
        val adminEmailList = userRepo.findAllAdminEmail()

        assertTrue(adminEmailList.size == 2)
    }
}