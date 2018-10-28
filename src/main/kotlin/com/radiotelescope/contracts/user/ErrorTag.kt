package com.radiotelescope.contracts.user

/**
 * Enum representing field validation failures for the User Entity
 */
enum class ErrorTag {
    ID,
    FIRST_NAME,
    LAST_NAME,
    EMAIL,
    COMPANY,
    PASSWORD,
    PASSWORD_CONFIRM,
    PROFILE_PICTURE,
    ACTIVE,
    STATUS,
    CATEGORY_OF_SERVICE,
    PAGE_PARAMS,
    ROLES
}