package com.radiotelescope.service.ses

/**
 * Enum representing field validation failures for the email sending
 */
enum class ErrorTag {
    SEND_EMAIL,
    FROM_ADDRESS,
    EMAIL_BODY,
    EMAIL_SUBJECT,
    TO_ADDRESSES
}