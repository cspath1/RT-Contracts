package com.radiotelescope.service.sns

/**
 * Enum representing field validation failures for the sending notifications
 */
enum class ErrorTag {
    TO_NUMBER,
    TOPIC,
    MESSAGE,
    SEND_MESSAGE,
    TYPE,
    PROTOCOL
}