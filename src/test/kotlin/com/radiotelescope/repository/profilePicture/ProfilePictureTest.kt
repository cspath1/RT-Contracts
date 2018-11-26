package com.radiotelescope.repository.profilePicture

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class ProfilePictureTest {
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
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var profilePictureRepo: IProfilePictureRepository

    private lateinit var user: User
    private lateinit var approvedProfilePicture: ProfilePicture

    @Before
    fun setUp() {
        // Create a user
        user = testUtil.createUser(
                email = "cspath1@ycp.edu",
                accountHash = "Test Account"
        )

        // Create an approved profile picture for the user
        approvedProfilePicture = testUtil.createProfilePicture(
                user = user,
                url = ProfilePicture.DEFAULT_PROFILE_PICTURE,
                approved = true
        )
    }

    @Test
    fun testFindUsersActiveProfilePicture() {
        val theProfilePicture = profilePictureRepo.findUsersActiveProfilePicture(
                userId = user.id
        )

        assertNotNull(theProfilePicture)
        assertEquals(approvedProfilePicture.validated, theProfilePicture.validated)
        assertEquals(approvedProfilePicture.profilePictureUrl, theProfilePicture.profilePictureUrl)
        assertEquals(approvedProfilePicture.id, theProfilePicture.id)
        assertEquals(approvedProfilePicture.user, theProfilePicture.user)
    }
}