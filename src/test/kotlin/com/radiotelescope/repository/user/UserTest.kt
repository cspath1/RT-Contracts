package com.radiotelescope.repository.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.model.user.Filter
import com.radiotelescope.repository.model.user.SearchCriteria
import com.radiotelescope.repository.model.user.UserSpecificationBuilder
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
internal class UserTest {
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
    private lateinit var userRoleRepository: IUserRoleRepository

    private var email: String = ""
    private lateinit var user: User

    @Before
    fun setUp() {
        // Instantiate and persist a User Entity Object
        user = testUtil.createUser("cspath1@ycp.edu")
        testUtil.createUserRolesForUser(user, UserRole.Role.MEMBER, true)
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

    @Test
    fun findAllNonAdminUser() {
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = false
        )

        val userPage = userRepo.findAllNonAdminUsers(PageRequest.of(0, 25))

        assertNotNull(userPage)
        assertEquals(1, userPage.content.size)

        // Should be the email of the non-admin user
        assertEquals(email, userPage.content[0].email)
    }

    @Test
    fun testSearchEmail() {
        val searchCriteria = SearchCriteria(Filter.EMAIL, "rpim")
        val specification = UserSpecificationBuilder().with(searchCriteria).build()

        val userList = userRepo.findAll(specification)

        assertNotNull(userList)
        assertEquals(2, userList.size)

        for (user in userList) {
            assertTrue(user.email.contains("rpim"))
        }
    }

    @Test
    fun testSearchFirstNameOrLastName() {
        val searchCriteriaOne = SearchCriteria(Filter.FIRST_NAME, "Fir")
        val searchCriteriaTwo = SearchCriteria(Filter.LAST_NAME, "La")

        val specification = UserSpecificationBuilder().with(searchCriteriaOne).with(searchCriteriaTwo).build()

        val userList = userRepo.findAll(specification)

        assertNotNull(userList)
        assertEquals(3, userList.size)
    }

    @Test
    fun testSearchCompanyName() {
        val user = userRepo.findByEmail(email)!!
        user.company = "York College of PA"

        // Should still return the above user
        val searchCriteria = SearchCriteria(Filter.COMPANY, "york college")

        val specification = UserSpecificationBuilder().with(searchCriteria).build()

        val userList = userRepo.findAll(specification)

        assertNotNull(userList)
        assertEquals(1, userList.size)
        assertEquals(user.email, userList[0].email)
    }
}