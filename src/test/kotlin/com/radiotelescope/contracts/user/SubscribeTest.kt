package com.radiotelescope.contracts.user

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.userNotificationType.SetEmail
import com.radiotelescope.contracts.userNotificationType.SetPhone
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository
import liquibase.integration.spring.SpringLiquibase
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.junit.*
import org.junit.Assert.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class SubscribeTest {
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

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    @Autowired
    private lateinit var userNotificationTypeRepo: IUserNotificationTypeRepository

    private val baseRequest = Register.Request(
            firstName = "John",
            lastName = "Doe",
            email = "YCPtestemail@gmail.com",
            emailConfirm = "YCPtestemail@gmail.com",
            phoneNumber = "907-867-5390",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of Pennsylvania",
            categoryOfService = UserRole.Role.STUDENT
    )

    private val basePhoneRequest = Register.Request(
            firstName = "John",
            lastName = "Doe",
            email = "YCPtestemail@gmail.com",
            emailConfirm = "YCPtestemail@gmail.com",
            phoneNumber = "717-867-5309",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of Pennsylvania",
            categoryOfService = UserRole.Role.STUDENT
    )

    @Test
    fun subscribePhoneTest(){
        val (registerToken, registerError) = Register(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                userNotificationTypeRepo = userNotificationTypeRepo
        ).execute()
        assertNull(registerError)

        val (setEmailToken, setEmailError) = SetPhone(
                id = userRepo.findByEmail(baseRequest.email)!!.id,
                userNotificationTypeRepo = userNotificationTypeRepo
        ).execute()
        assertNull(setEmailError)

        val (token, error) = Subscribe(
                userRepo = userRepo,
                id = registerToken!!.id,
                userNotificationType = userNotificationTypeRepo
        ).execute()

        assertNull(error)
    }

    @Test
    fun subscribeTest(){
        val (registerToken, registerError) = Register(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                userNotificationTypeRepo = userNotificationTypeRepo
        ).execute()
        assertNull(registerError)

        val (token, error) = Subscribe(
                userRepo = userRepo,
                id = registerToken!!.id,
                userNotificationType = userNotificationTypeRepo
        ).execute()

        assertNull(error)
    }
}