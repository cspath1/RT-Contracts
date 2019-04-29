package com.radiotelescope.contracts.userNotificationType

import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.user.Register
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository
import com.radiotelescope.repository.userNotificationType.UserNotificationType
import liquibase.integration.spring.SpringLiquibase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.junit.Assert.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class SetPhoneTest {

    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var userNotificationTypeRepo: IUserNotificationTypeRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private val baseRequest = Register.Request(
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


    @Test
    fun testSetPhone(){
        val (registerToken, registerError) = Register(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                userNotificationTypeRepo = userNotificationTypeRepo
        ).execute()

        assertNull(registerError)

        assertEquals(userNotificationTypeRepo.findById(userRepo.findByEmail(baseRequest.email)!!.id).get().type, UserNotificationType.NotificationType.EMAIL)

        val(phoneToken, phoneError) = SetPhone(
                id = userRepo.findByEmail(baseRequest.email)!!.id,
                userNotificationTypeRepo = userNotificationTypeRepo
        ).execute()

        assertNull(phoneError)
        assertEquals(userNotificationTypeRepo.findById(userRepo.findByEmail(baseRequest.email)!!.id).get().type, UserNotificationType.NotificationType.PHONE)
    }
}