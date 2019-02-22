package com.radiotelescope.repository.model.user

import com.radiotelescope.repository.user.User

/**
 * Enum class that acts as a search filter for the [User] entity
 *
 * @param field the corresponding entity field. Note: NOT the corresponding SQL column
 * @param label the filter label
 */
enum class Filter(val field: String, val label: String) {
    FIRST_NAME("firstName", "First Name"),
    LAST_NAME("lastName", "Last Name"),
    EMAIL("email", "Email Address"),
    COMPANY("company", "Company");

    companion object {
        /**
         * Takes a string value and will adapt it into the corresponding
         * [Filter] value. If the string supplied does not match with
         * any of the values, it will return null
         *
         * @param field the [User] field
         * @return a [Filter] object or null
         */
        fun fromField(field: String): Filter? {
            return when (field) {
                "firstName" -> FIRST_NAME
                "lastName" -> LAST_NAME
                "email" -> EMAIL
                "company" -> COMPANY
                else -> {
                    // Handle the case where an invalid parameter
                    // is supplied
                    null
                }
            }
        }
    }
}