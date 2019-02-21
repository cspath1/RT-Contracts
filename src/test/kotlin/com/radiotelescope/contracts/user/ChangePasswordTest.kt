package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import liquibase.integration.spring.SpringLiquibase

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class ChangePasswordTest {
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
    private lateinit var userRepo: IUserRepository

    private var userId = -1L

    @Before
    fun setUp() {
        // Persist a user
        val theUser = testUtil.createUserWithEncodedPassword(
                email = "cspath1@ycp.edu",
                password = "Password1@",
                accountHash = "Test Account 1"
        )
        userId = theUser.id
    }

    @Test
    fun changeUserPasswordTest(){
        val theUser = userRepo.findById(userId)

        val request = ChangePassword.Request(
                currentPassword = "Password1@",
                password = "newPassword123",
                passwordConfirm = "newPassword123",
                id = theUser.get().id

        )
        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        val updatedUser = userRepo.findById(userId)

        assertNull(errors)
        assertEquals(id, request.id)
        assertNotEquals(updatedUser.get().password, request.currentPassword)
    }

    @Test
    fun changePasswordInvalidUserId() {
        val request = ChangePassword.Request(
                currentPassword = "Password1@",
                password = "Password1!",
                passwordConfirm = "Password1!",
                id = 311L
        )

        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        assertNotNull(errors)
        assertNull(id)
        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun changePasswordToSameAsCurrentPasswordTest(){
        val theUser = userRepo.findById(userId)

        val request = ChangePassword.Request(
                currentPassword = "Password1@",
                password = "Password1@",
                passwordConfirm = "Password1@",
                id = theUser.get().id
        )
        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        assertNotNull(errors)
        assertNull(id)
        assertTrue(errors!![ErrorTag.CURRENT_PASSWORD].isNotEmpty())
    }

    @Test
    fun changePasswordDifferentPasswordConfirmTest(){
        val theUser = userRepo.findById(userId)

        val request = ChangePassword.Request(
                currentPassword = "Password1@",
                password = "userPassword123",
                passwordConfirm = "userPassword1234",
                id = theUser.get().id
        )
        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        assertNotNull(errors)
        assertNull(id)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun changePasswordInsufficientPasswordTest(){
        val theUser = userRepo.findById(userId)

        val request = ChangePassword.Request(
                currentPassword = "Password1@",
                password = "fail",
                passwordConfirm = "fail",
                id = theUser.get().id
        )
        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        assertNotNull(errors)
        assertNull(id)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun changePasswordBlankCurrentPasswordTest(){
        val theUser = userRepo.findById(userId)

        val request = ChangePassword.Request(
                currentPassword = "",
                password = "userPassword123",
                passwordConfirm = "userPassword123",
                id = theUser.get().id
        )
        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        assertNotNull(errors)
        assertNull(id)
        assertTrue(errors!![ErrorTag.CURRENT_PASSWORD].isNotEmpty())
    }

    @Test
    fun changePasswordBlankPasswordTest() {
        val theUser = userRepo.findById(userId)

        val request = ChangePassword.Request(
                currentPassword = "NewPassword1@",
                password = " ",
                passwordConfirm = "NewPassword1!",
                id = theUser.get().id
        )

        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        assertNotNull(errors)
        assertNull(id)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun changePasswordBlankPasswordConfirmTest() {
        val theUser = userRepo.findById(userId)

        val request = ChangePassword.Request(
                currentPassword = "Password1@",
                password = "NewPassword1!",
                passwordConfirm = " ",
                id = theUser.get().id
        )

        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        assertNotNull(errors)
        assertNull(id)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun changePasswordInvalidCurrentPasswordTest() {
        val theUser = userRepo.findById(userId)

        val request = ChangePassword.Request(
                currentPassword = "Password1!",
                password = "NewPassword1!",
                passwordConfirm = "NewPassword1!",
                id = theUser.get().id
        )

        val (id, errors) = ChangePassword(
                userRepo = userRepo,
                request = request
        ).execute()

        assertNotNull(errors)
        assertNull(id)
        assertTrue(errors!![ErrorTag.CURRENT_PASSWORD].isNotEmpty())
    }
}