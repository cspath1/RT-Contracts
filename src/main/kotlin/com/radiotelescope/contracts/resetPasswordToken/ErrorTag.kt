package com.radiotelescope.contracts.resetPasswordToken

/**
 * Enum representing field validation failures for the ResetPasswordToken Entity
 */
enum class ErrorTag {
    ID,
    USER_ID,
    EMAIL,
    TOKEN,
    EXPIRATION_DATE,
    PASSWORD,
    PASSWORD_CONFIRM
}