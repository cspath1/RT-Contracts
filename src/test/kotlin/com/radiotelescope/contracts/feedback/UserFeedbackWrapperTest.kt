package com.radiotelescope.contracts.feedback

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.feedback.IFeedbackRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.FakeUserContext
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserFeedbackWrapperTest : AbstractSpringTest() {
    @Autowired
    private lateinit var feedbackRepo: IFeedbackRepository

    private var pageable = PageRequest.of(0, 5)

    private val baseRequest = Create.Request(
            name = "Michael Scarn",
            priority = 10,
            comments = "Threat Level Midnight"
    )

    private lateinit var admin: User

    val context = FakeUserContext()
    private lateinit var factory: FeedbackFactory
    private lateinit var wrapper: UserFeedbackWrapper

    @Before
    fun init() {
        // Instantiate the factory and wrapper
        factory = BaseFeedbackFactory(feedbackRepo)

        wrapper = UserFeedbackWrapper(
                context = context,
                factory = factory
        )

        admin = testUtil.createUser("admin@ycpradiotelescope.com")
        testUtil.createUserRoleForUser(admin, UserRole.Role.ADMIN, true)
    }

    @Test
    fun testValidFeedbackCreation() {
        val (id, error) = wrapper.create(baseRequest).execute()

        assertNotNull(id)
        assertNull(error)
    }

    @Test
    fun list_Success() {
        context.login(admin.id)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.list(pageable) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun list_NotValidated_Failure() {
        // Do not log the user in
        val error = wrapper.list(pageable) {
            assertNull(it.success)
            assertNotNull(it.error)
        }

        assertNotNull(error)
    }
}