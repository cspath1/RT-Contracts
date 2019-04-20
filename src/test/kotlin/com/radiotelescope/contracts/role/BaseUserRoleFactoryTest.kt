package com.radiotelescope.contracts.role

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseUserRoleFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    private lateinit var factory: UserRoleFactory

    @Before
    fun init() {
        factory = BaseUserRoleFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    @Test
    fun unapprovedList() {
        // Call the factory method
        val cmd = factory.unapprovedList(
                pageable = PageRequest.of(0, 5)
        )

        // Ensure it is the correct command
        assertTrue(cmd is UnapprovedList)
    }

    @Test
    fun validate() {
        // Call the factory method
        val cmd = factory.validate(
                request = Validate.Request(
                        id = 1L,
                        role = UserRole.Role.MEMBER
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Validate)
    }

    @Test
    fun retrieve() {
        // Call the factory method
        val cmd = factory.retrieve(
                id = 1L
        )

        // Ensure it is the correct command
        assertTrue(cmd is Retrieve)
    }

    @Test
    fun requestRole() {
        // Call the factory method
        val cmd = factory.requestRole(
                request = RequestRole.Request(
                        userId = testUtil.createUser("lferree@ycp.edu").id,
                        role = UserRole.Role.GUEST
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is RequestRole)
    }
}