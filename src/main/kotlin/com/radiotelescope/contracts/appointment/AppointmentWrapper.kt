package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.UserFactory
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import com.radiotelescope.contracts.appointment.AppointmentInfo
import com.radiotelescope.security.crud.AppointmentPageable
import com.radiotelescope.security.crud.AppointmentRetrievable

class AppointmentWrapper(
        private val context: UserContext,
        private val factory: UserFactory
    ) //: AppointmentRetrievable<Long, SimpleResult<AppointmentInfo, Multimap<ErrorTag, String>>>
{
         fun pageableAppointments(request: Pageable, withAccess: (result: SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>>) -> Unit):Unit {
            if (context.currentUserId() != null)
                context.require(requiredRoles = listOf(UserRole.Role.ADMIN), successCommand = factory.list(request)).execute()
    }
}