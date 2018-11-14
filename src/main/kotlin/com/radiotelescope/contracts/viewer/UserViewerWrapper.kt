package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Pageable

/**
Wrapper class used in the Controller classes for Viewer. Includes a context object to ensure that Researchers have the ability
 to have viewers to their private appointments.
 @param context [UserContext]
 @param viewerFactory [ViewerFactory]
 @return [Unit]
 */
class UserViewerWrapper(
        private val context: UserContext,
        private val viewerFactory: ViewerFactory
        )
{
    fun getViewersByUserId(request:Create.Request, pageable: Pageable, withAccess: (result: SimpleResult<MutableList<Appointment>, Multimap<ErrorTag, String>>) -> Unit): AccessReport?
    {
            context.require(
                    requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                    successCommand = viewerFactory.retrieveViewersByUserId(request, pageable)
            ).execute(withAccess)
        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    fun getViewersByAppointmentId(appt_id: Long, pageable:Pageable, withAccess: (result: SimpleResult<MutableList<Appointment?>, Multimap<ErrorTag, String>>)->Unit):AccessReport?
    {
        context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                successCommand = viewerFactory.retrieveViewersByAppointmentId(appt_id, pageable)
        ).execute(withAccess)
        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    fun create(request: Create.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit):AccessReport?
    {
        context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                successCommand = viewerFactory.create(request)
        ).execute(withAccess)
        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }
}