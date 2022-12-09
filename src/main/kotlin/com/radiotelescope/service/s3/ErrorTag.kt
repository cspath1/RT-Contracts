package com.radiotelescope.service.s3

/**
 * Enum class representing errors pertaining to S3 operations
 */
enum class ErrorTag {
    UPLOAD,
    NO_SUCH_KEY,
    RETRIEVE,
    DELETE
}