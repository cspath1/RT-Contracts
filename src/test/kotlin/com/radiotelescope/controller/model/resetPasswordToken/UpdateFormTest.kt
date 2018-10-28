package com.radiotelescope.controller.model.resetPasswordToken

import com.radiotelescope.contracts.resetPasswordToken.ErrorTag
import org.junit.Assert.*
import org.junit.Test

class UpdateFormTest {


    @Test
    fun testValid_CorrectContraints_Success(){
        // Call the validate request method
        val errors = UpdateForm(
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1"
        ).validateRequest()

        // Make sure no errors occured
        assertNull(errors)
    }

    @Test
    fun testInvalid_BlankPassword_Failure(){
        // Call the validate request method
        val errors = UpdateForm(
                password = "",
                passwordConfirm = ""
        ).validateRequest()

        // Make sure it failed
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testInvalid_PasswordsDoNotMatch_Failure(){
        // Call the validate request method
        val errors = UpdateForm(
                password = "ValidPassword1",
                passwordConfirm = "InvalidPassword1"
        ).validateRequest()

        // Make sure it failed
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testInvalid_PasswordRegexNotAMatch_Failure(){
        // Call the validate request method
        val errors = UpdateForm(
                password = "password",
                passwordConfirm = "password"
        ).validateRequest()

        // Make sure it failed
        assertNotNull(errors)
        assertTrue(errors!![ErrorTag.PASSWORD].isNotEmpty())
    }
}