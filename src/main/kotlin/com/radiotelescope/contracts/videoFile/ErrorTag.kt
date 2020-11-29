package com.radiotelescope.contracts.videoFile

/**
 * Enum representing field validation failures for the Video File Entity
 */
enum class ErrorTag {
    ID,
    THUMBNAIL_PATH,
    VIDEO_PATH,
    VIDEO_LENGTH,
    RECORD_CREATED_TIMESTAMP,
    RECORD_UPDATED_TIMESTAMP,
    TOKEN,
    PAGE_PARAMS
}