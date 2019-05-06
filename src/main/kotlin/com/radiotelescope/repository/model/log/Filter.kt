package com.radiotelescope.repository.model.log

import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.User

/**
 * Enum class that acts as search filter for the [Log] entity
 *
 * @param field the corresponding entity field. Note: NOT the corresponding SQL column
 * @param label the filter label
 */
enum class Filter(val field: String, val label: String) {
    ACTION("action", "Action"),
    AFFECTED_TABLE("affectedTable", "Affected Table"),
    IS_SUCCESS("isSuccess", "Is Success"),
    STATUS("status", "Status");

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
                "action" -> ACTION
                "affectedTable" -> AFFECTED_TABLE
                "isSuccess" -> IS_SUCCESS
                "status" -> STATUS
                else -> {
                    // Handle the case where an invalid parameter
                    // is supplied
                    null
                }
            }
        }
    }
}