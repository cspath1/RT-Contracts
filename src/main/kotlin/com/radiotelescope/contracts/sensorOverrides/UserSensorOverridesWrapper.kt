package com.radiotelescope.contracts.sensorOverrides

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.sensorOverrides.SensorOverrides
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [SensorOverridesFactory] and is responsible for all
 * user role validations for the [SensorOverrides] Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [SensorOverridesFactory] factory interface
 */
class UserSensorOverridesWrapper (
        private val context: UserContext,
        private val factory: SensorOverridesFactory
) {

    /**
     * Wrapper method for the [SensorOverridesFactory.retrieveList] method.
     *
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieveList(withAccess: (result: SimpleResult<List<SensorOverrides>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.retrieveList()
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [SensorOverridesFactory.retrieve] method.
     *
     * @param sensorName the name of the sensor to update
     * @param overridden the sensor overridden status
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun update(sensorName: String, overridden: Boolean, withAccess: (result: SimpleResult<SensorOverrides, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.update(sensorName, overridden)
        ).execute(withAccess)
    }
}