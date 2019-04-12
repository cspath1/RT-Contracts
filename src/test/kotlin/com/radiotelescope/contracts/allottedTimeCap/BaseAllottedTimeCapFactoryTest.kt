package com.radiotelescope.contracts.allottedTimeCap

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseAllottedTimeCapFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private lateinit var factory: AllottedTimeCapFactory

    @Before
    fun init() {
        // Instantiate the factory
        factory = BaseAllottedTimeCapFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    @Test
    fun update() {
        // Call the factory method
        val cmd = factory.update(
                request = Update.Request(
                        userId = 1234L,
                        allottedTime = 1L
                )
        )

        // Ensure it is the correct command
        Assert.assertTrue(cmd is Update)
    }
}
