package com.radiotelescope.contracts.updateEmailToken

/**
 * Enum representing field validation failures for the UpdateEmailToken Entity
 */
enum class ErrorTag {
    TOKEN,
    EXPIRATION_DATE,
    EMAIL,
    EMAIL_CONFIRM,
    USER_ID
}