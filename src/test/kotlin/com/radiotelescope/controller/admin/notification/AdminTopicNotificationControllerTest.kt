package com.radiotelescope.controller.admin.notification

import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import com.radiotelescope.services.sns.MockAwsSnsService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class AdminTopicNotificationControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var roleRepo: IUserRoleRepository

    private lateinit var adminTopicNotificationController: AdminTopicNotificationController

    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        adminTopicNotificationController = AdminTopicNotificationController(
                context = getContext(),
                roleRepo = roleRepo,
                awsSnsService = MockAwsSnsService(true),
                logger = getLogger()
        )

        adminTopicNotificationController.defaultSendTopic = "testARN"

        user = testUtil.createUser("jhorne@ycp.edu")
    }

    @Test
    fun testSuccessResponseDefaultTopic() {
        getContext().login(user.id)
        // Make user an admin
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(user, UserRole.Role.ADMIN, true)

        val result = adminTopicNotificationController.execute(
                topic = null,
                message = "Test Message"
        )

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
    }

    @Test
    fun testSuccessResponseOtherTopic() {
        getContext().login(user.id)
        // Make user an admin
        getContext().currentRoles.add(UserRole.Role.ADMIN)
        testUtil.createUserRoleForUser(user, UserRole.Role.ADMIN, true)

        val result = adminTopicNotificationController.execute(
                topic = "testARN",
                message = "Test Message"
        )

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = adminTopicNotificationController.execute(
                topic = "testARN",
                message = "Test Message"
        )

        assertNotNull(result)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertNull(result.errors)
    }
}