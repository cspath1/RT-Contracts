package com.radiotelescope.contracts.role

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
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
internal class UserRoleInfoTest {
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
    private lateinit var userRepo: IUserRepository

    private lateinit var userInfo: UserInfo

    private lateinit var user: User

    @Before
    fun setUp() {
        user = User(
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                password = "Password"
        )

        user.phoneNumber = "717-823-2216"
        user.company = "York College of PA"
        user.active = true
        user.status = User.Status.ACTIVE
        user.id = 1L

        userInfo = UserInfo(
                user = user,
                userRoleLabel = UserRole.Role.GUEST.label,
                allottedTime = 5*60*60*1000
        )

        userRepo.save(user)
    }

    @Test
    fun testPrimaryConstructor() {
        val userRoleInfo = UserRoleInfo(
                id = 1L,
                role = UserRole.Role.GUEST,
                userInfo = userInfo
        )

        assertEquals(1L, userRoleInfo.id)
        assertEquals(UserRole.Role.GUEST, userRoleInfo.role)
        assertEquals(userInfo, userRoleInfo.userInfo)
    }

    @Test
    fun testSecondaryConstructor() {
        val userRole = UserRole(
                user = user,
                role = UserRole.Role.GUEST
        )

        userRole.id = 1L

        val userRoleInfo = UserRoleInfo(
                userRole = userRole,
                userInfo = userInfo
        )

        assertEquals(userRole.id, userRoleInfo.id)
        assertEquals(userRole.role, userRoleInfo.role)
        assertEquals(userInfo, userRoleInfo.userInfo)
    }
}