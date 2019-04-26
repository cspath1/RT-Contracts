package com.radiotelescope.contracts.user

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.radiotelescope.TestUtil
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
            phoneNumber = "717-867-5309",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of Pennsylvania",
            categoryOfService = UserRole.Role.STUDENT
    )

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
                userRoleRepo = userRoleRepo,
                userNotificationType = userNotificationTypeRepo
        ).execute()

        assertNull(error)

        val builder = AmazonSNSClientBuilder.standard().withRegion("us-east-2").build()

        builder.listSubscriptionsByTopic(builder.createTopic("UserTopic" + registerToken.id).topicArn)

        var topicARN = builder.createTopic("UserTopic" + registerToken.id).topicArn
        System.out.println("\n\n\n")
        System.out.println(builder.listSubscriptions(builder.listSubscriptionsByTopic(topicARN).nextToken).subscriptions.size)
//        System.out.println(builder.listSubscriptionsByTopic(topicARN).subscriptions.get(0).subscriptionArn)
        System.out.println("\n\n\n")

    }
}