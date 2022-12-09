package com.radiotelescope.security

import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.security.service.UserDetailsImpl
import org.springframework.security.core.GrantedAuthority

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserDetailsTest : AbstractSpringTest() {

    private lateinit var userDetails: UserDetailsImpl

    private lateinit var user: User

    @Before
    fun init(){
        // Create a user
        // Status is auto set to ACTIVE in the default create user function
        user = testUtil.createUser("vmaresca@ycp.edu")
        user.active = true
    }

    @Test
    fun testUserDetailsGetMethods(){
        var arrayList = HashSet<GrantedAuthority>()
        arrayList.add(GrantedAuthority { "ROLE_USER" })
        var grantedAuthorities: Set<GrantedAuthority> = arrayList

        userDetails = UserDetailsImpl(
                user = user,
                grantedAuthorities = grantedAuthorities
        )

        assertNotNull(userDetails)
        assertFalse(userDetails.isCredentialsNonExpired)
        assertFalse(userDetails.isAccountNonExpired)

        // userDetails.authorities gets tested in UserDetailsServiceTest
        //assertEquals("[ROLE_USER]", userDetails.authorities)
        // Username == email address
        assertEquals(user.email, userDetails.username)
        assertEquals(user.password, userDetails.password)

        assertTrue(userDetails.isAccountNonLocked)
        assertTrue(userDetails.isEnabled)
    }
}