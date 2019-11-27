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
 * @param factory the [SensorStatusFactory] interface
 */
class UserSensorStatusWrapper(
        private val context: UserContext,
        private val factory: SensorStatusFactory
) {
    /**
     * Wrapper method for the [SensorStatusFactory.create] method.
     *
     * @param request the [Create.Request] object
     */
    fun create(request: Create.Request, uuid: String): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request, uuid)
    }

    fun retrieve(id: Long, withAccess: (result: SimpleResult<SensorStatus, Multimap<ErrorTag, String>>) -> Unit): AccessReport?  {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.retrieve(id)
        ).execute(withAccess)
    }
}