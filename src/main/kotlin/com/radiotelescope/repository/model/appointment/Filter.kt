package com.radiotelescope.repository.model.appointment

import com.radiotelescope.repository.appointment.Appointment

/**
 * Enum class that acts as a search filter for the [Appointment]
 *
 * @param field the filter field parameter
 * @param multiCompatible whether the filter is compatible with other filters or not
 */
enum class Filter(val field: String, val multiCompatible: Boolean) {
    // User full name will operate different, and will be an "AND" clause
    USER_FULL_NAME("userFullName", false),
    USER_FIRST_NAME("firstName", true),
    USER_LAST_NAME("lastName", true),
    USER_EMAIL("email", true),
    USER_COMPANY("company", true);

    companion object {
        /**
         * Takes a string value and will adapt it into the corresponding
         * [Filter] value. If the string supplied does not match any of the
         * [Filter] values, it will instead return null
         *
         * @param field the search field
         * @return a [Filter] object or null
         */
        fun fromField(field: String): Filter? {
            return when (field) {
                "userFullName" -> USER_FULL_NAME
                "userLastName" -> USER_LAST_NAME
                "userFirstName" -> USER_FIRST_NAME
                "userEmail" -> USER_EMAIL
                "userCompany" -> USER_COMPANY
                else -> {
                    null
                }
            }
        }

        /**
         * Returns a list of the appointment's user-specific search parameters
         *
         * @return a [List] of [Filter] objects specific to user params
         */
        fun userSearchParams(): List<Filter> {
            return listOf(USER_FULL_NAME, USER_LAST_NAME, USER_FIRST_NAME, USER_EMAIL, USER_COMPANY)
        }
    }
}