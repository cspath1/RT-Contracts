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

    @DataJpaTest
    @RunWith(SpringRunner::class)
    @ActiveProfiles(value = ["test"])
    internal class ChangePasswordTest {
        @TestConfiguration
        class UtilTestContextConfiguration {
            @Bean
            fun utilService(): TestUtil { return TestUtil() }
        }

        @Autowired
        private lateinit var testUtil: TestUtil

        @Autowired
        private lateinit var userRepo: IUserRepository

        private var userId = -1L
        private var userId2 = -2L

        @Before
        fun setUp() {
            // Persist a user
            val theUser = testUtil.createUser("cspath1@ycp.edu")
            userId = theUser.id
        }

        @Test
        fun changeUserPasswordTest(){
            val theUser = userRepo.findById(userId)

            val request = ChangePassword.Request(
                    currentPassword = theUser.get().password,
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
        fun changePasswordToSameAsCurrentPasswordTest(){
            val theUser = userRepo.findById(userId)

            val request = ChangePassword.Request(
                    currentPassword = theUser.get().password,
                    password = theUser.get().password,
                    passwordConfirm = theUser.get().password,
                    id = theUser.get().id
            )
            val (id, errors) = ChangePassword(
                    userRepo = userRepo,
                    request = request
            ).execute()

            assertNotNull(errors)
            assertNull(id)
        }

        @Test
        fun changePasswordDifferentPasswordConfirmTest(){
            val theUser = userRepo.findById(userId)

            val request = ChangePassword.Request(
                    currentPassword = theUser.get().password,
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
        }

        @Test
        fun changePasswordInsufficientPasswordTest(){
            val theUser = userRepo.findById(userId)

            val request = ChangePassword.Request(
                    currentPassword = theUser.get().password,
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
        }

        @Test
        fun changePasswordBlankPasswordTest(){
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

        }
    }