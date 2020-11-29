package com.radiotelescope.contracts.user

/**
 * Enum representing field validation failures for the User Entity
 */
enum class ErrorTag {
    ID,
    FIRST_NAME,
    LAST_NAME,
    EMAIL,
    EMAIL_CONFIRM,
    COMPANY,
    PASSWORD,
    PASSWORD_CONFIRM,
    PROFILE_PICTURE,
    ACTIVE,
    STATUS,
    CATEGORY_OF_SERVICE,
    PAGE_PARAMS,
    ROLES,
    TIME,
    CURRENT_PASSWORD,
    SEARCH,
    LOGIN_ATTEMPT,
    UPLOAD_PROFILE_PICTURE,
    NOTIFICATION_TYPE
}