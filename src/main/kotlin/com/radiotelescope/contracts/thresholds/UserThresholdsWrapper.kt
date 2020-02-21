package com.radiotelescope.contracts.thresholds

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.thresholds.Thresholds
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [ThresholdsFactory] and is responsible for all
 * user role validations for the Thresholds Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [ThresholdsFactory] factory interface
 */
class UserThresholdsWrapper (
        private val context: UserContext,
        private val factory: ThresholdsFactory
) {

    /**
     * Wrapper method for the [ThresholdsFactory.retrieve] method.
     *
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(sensorName: String, withAccess: (result: SimpleResult<Thresholds, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.retrieve(sensorName)
        ).execute(withAccess)
    }
}