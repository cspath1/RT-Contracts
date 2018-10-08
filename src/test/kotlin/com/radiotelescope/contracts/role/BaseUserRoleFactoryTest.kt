package com.radiotelescope.contracts.role

import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class BaseUserRoleFactoryTest {

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private lateinit var factory: UserRoleFactory

    @Before
    fun init() {
        factory = BaseUserRoleFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
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
}