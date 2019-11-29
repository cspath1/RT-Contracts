package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.sensorStatus.SensorStatus
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [SensorStatusFactory] and is responsible for all
 * user role validations for the Feedback Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [SensorStatusFactory] factory interface
 */
class UserSensorStatusWrapper(
        private val context: UserContext,
        private val factory: SensorStatusFactory
) {
    /**
     * Wrapper method for the [SensorStatusFactory.create] method.
     *
     * @param request the [Create.Request] object
     * @param uuid the uuid used by the control room app
     * @return A [Command] object
     */
    fun create(request: Create.Request, uuid: String, profile: String): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request, uuid, profile)
    }

    /**
     * Wrapper method for the [SensorStatusFactory.retrieve] method.
     *
     * @param id the id of the sensor_status record
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(id: Long, withAccess: (result: SimpleResult<SensorStatus, Multimap<ErrorTag, String>>) -> Unit): AccessReport?  {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.retrieve(id)
        ).execute(withAccess)
    }
}