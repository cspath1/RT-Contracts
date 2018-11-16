package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import junit.framework.Assert.fail
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private var pageable = PageRequest.of(0, 5)

    @Before
    fun setUp() {
        // Create a few user's
        testUtil.createUser(
                email = "cspath1@ycp.edu",
                accountHash = "Test Account 1"
        )
        testUtil.createUser(
                email = "spathcody@gmail.com",
                accountHash = "Test Account 2"
        )
        testUtil.createUser(
                email = "codyspath@gmail.com",
                accountHash = "Test Account 3"
        )
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



    @Test
    fun ensureAdminsFilteredOut()
    {
        userRepo.deleteAll()

        val u: User = testUtil.createUser("hello@ycp.edu")
        testUtil.createUserRolesForUser(u.id, UserRole.Role.ADMIN , true )

        val u2:User = testUtil.createUser("hello2@ycp.edu")
        testUtil.createUserRolesForUser(u2.id, UserRole.Role.RESEARCHER , true )

        val (page, errors) = List(
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        val size:Int? = page?.content?.size
        val sizeSmart = size!!
        println("sizeSmart is: " + sizeSmart)
        if (errors != null)
            fail()
        else
        {
            for (i in 0.. sizeSmart - 1) {
                println("membership role is, to String: " + page.content.get(i).membershipRole.toString())
                //println("UserRole.Role.MEMBER.toString(): " + UserRole.Role.MEMBER.toString())
                if (page.content.get(i).membershipRole.toString().equals(UserRole.Role.ADMIN.toString()))
                    fail()
            }
        }
    }
}