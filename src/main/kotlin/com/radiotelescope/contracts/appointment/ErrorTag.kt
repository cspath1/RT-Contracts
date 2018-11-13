package com.radiotelescope.contracts.appointment

/**
 * Enum representing field validation failures for the Appointment Entity
 */
enum class ErrorTag{
    ID,
    USER_ID,
    START_TIME,
    END_TIME,
    STATUS,
    TELESCOPE_ID,
    PUBLIC,
    PAGE_PARAMS,
    CATEGORY_OF_SERVICE,
    ALLOTTED_TIME,
    OVERLAP,
    RIGHT_ASCENSION,
    DECLINATION
}