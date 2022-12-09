package com.radiotelescope.security

import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.security.service.UserDetailsImpl
import com.radiotelescope.security.service.UserDetailsServiceImpl

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserDetailsServiceTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository
    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var userDetailsService: UserDetailsServiceImpl

    private lateinit var user: User

    @Before
    fun init(){
        // Create a user
        // Status is auto set to ACTIVE in the default create user function
        user = testUtil.createUser("vmaresca@ycp.edu")

        userDetailsService = UserDetailsServiceImpl(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }

    @Test
    fun testSuccessfulExecute(){
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.USER,
                isApproved = true
        )
        // Username == email address
        var result = userDetailsService.loadUserByUsername(user.email)

        assertNotNull(result)
        println(result.authorities)
        assert(result is UserDetailsImpl)

    }
}