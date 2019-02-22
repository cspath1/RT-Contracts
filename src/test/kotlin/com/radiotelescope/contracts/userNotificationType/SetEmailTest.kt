package com.radiotelescope.contracts.userNotificationType

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.user.Register
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository
import org.springframework.test.context.ActiveProfiles
import java.util.*
import liquibase.integration.spring.SpringLiquibase
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class SetEmailTest {

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
    private lateinit var userNotificationTypeRepo : IUserNotificationTypeRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    @Autowired
    private lateinit var userRepo : IUserRepository

    private val baseUserRequest = Register.Request(
            firstName = "Cody",
            lastName = "Spath",
            email = "cspath1@ycp.edu",
            emailConfirm = "cspath1@ycp.edu",
            phoneNumber = "717-823-2216",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of Pennsylvania",
            categoryOfService = UserRole.Role.STUDENT
    )

    @Before
    fun setUp() {
        Register(
                userNotificationTypeRepo = userNotificationTypeRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                request = baseUserRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()
    }

    private val baseRequest = SetEmail.Request(
            userId = 1
    )

    @Test
    fun testValidConstraints_Success(){

        val (success, errors) = SetEmail(
               request = baseRequest,
                userNotificationTypeRepo = userNotificationTypeRepo
        ).execute()

        assertNull(errors)
        assertNotNull(success)
    }

    @Test
    fun testInvalidUserId_Failure(){
        val (success, errors) = SetEmail(
                request = SetEmail.Request(
                        userId = -1
                ),
                userNotificationTypeRepo = userNotificationTypeRepo
        ).execute()

        assertNull(success)
        assertNotNull(errors)
    }


}