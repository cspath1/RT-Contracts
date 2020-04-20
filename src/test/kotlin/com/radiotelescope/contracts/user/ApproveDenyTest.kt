package com.radiotelescope.contracts.user

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.contracts.user.ApproveDeny
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.services.s3.MockAwsS3DeleteService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ApproveDenyTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository
    private lateinit var baseRequest: ApproveDeny.Request
    private lateinit var deleteService: MockAwsS3DeleteService

    private var userId = -1L

    @Before
    fun init() {
        val theUser = testUtil.createUser("rpim@ycp.edu")

        userId = theUser.id
        deleteService = MockAwsS3DeleteService(true)

        baseRequest = ApproveDeny.Request(
                userId = userId,
                approved = true
        )
    }

    @Test
    fun testValidConstraintsApprove() {
        val (result, errors) = ApproveDeny(
                request = baseRequest,
                userRepo = userRepo,
                deleteService = deleteService
        ).execute()

        assertNotNull(result)
        assertNull(errors)

        val theUser = userRepo.findById(result!!.id).get()
        assertNotNull(theUser)
        theUser.profilePictureApproved?.let { assertTrue(it) }
    }

    @Test
    fun testValidConstraintsDeny() {
        val requestCopy = baseRequest.copy(
                approved = false
        )

        val (result, errors) = ApproveDeny(
                request = requestCopy,
                userRepo = userRepo,
                deleteService = deleteService
        ).execute()

        assertNotNull(result)
        assertNull(errors)

        // profile picture link and approval should be null
        assertNull(userRepo.findById(userId).get().profilePicture)
        assertNull(userRepo.findById(userId).get().profilePictureApproved)
    }
}