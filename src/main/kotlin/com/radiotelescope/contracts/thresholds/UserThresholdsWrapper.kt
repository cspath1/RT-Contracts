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
     * @param sensorName the name fo the sensor to retrieve
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(sensorName: String, withAccess: (result: SimpleResult<Thresholds, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.retrieve(sensorName)
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [ThresholdsFactory.retrieveList] method.
     *
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieveList(withAccess: (result: SimpleResult<List<Thresholds>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.retrieveList()
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [ThresholdsFactory.retrieve] method.
     *
     * @param sensorName the name of the sensor to update
     * @param maximum the maximum sensor value to update
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun update(sensorName: String, maximum: Double, withAccess: (result: SimpleResult<Thresholds, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.update(sensorName, maximum)
        ).execute(withAccess)
    }
}