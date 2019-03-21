package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert
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
@ActiveProfiles(value = ["test"])
internal class ListTest {
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
    private lateinit var userRoleRepo: IUserRoleRepository

    private var pageable = PageRequest.of(0, 5)

    @Before
    fun setUp() {
        // Create a few user's
        val user1 = testUtil.createUser("cspath1@ycp.edu")
        testUtil.createUserRolesForUser(user1, UserRole.Role.MEMBER, true)

        val user2 = testUtil.createUser("spathcody@gmail.com")
        testUtil.createUserRolesForUser(user2, UserRole.Role.RESEARCHER, true)

        val user3 = testUtil.createUser("codyspath@gmail.com")
        testUtil.createUserRolesForUser(user3, UserRole.Role.GUEST, true)
    }

    @Test
    fun testPopulatedRepo_Success() {
        val (page, errors) = List(
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        Assert.assertNull(errors)
        Assert.assertNotNull(page)
        Assert.assertEquals(3, page!!.content.size)
    }

    @Test
    fun testEmptyRepo_Success() {
        userRoleRepo.deleteAll()
        userRepo.deleteAll()

        val (page, errors) = List(
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        Assert.assertNotNull(page)
        Assert.assertNull(errors)
        Assert.assertEquals(0, page!!.content.size)
    }
}