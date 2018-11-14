package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.AppointmentFactory
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

class UserViewerWrapper(
        private val context: UserContext,
        private val viewerRepo: IViewerRepository,
        private val viewerFactory: ViewerFactory

        ):
{

    fun getViewersByUserId(request:Create.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport?
    {
            context.require(
                    requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                    successCommand = viewerRepo.getViewersOfAppointmentBySharingUserId(request.sharinguserId)
            ).execute(withAccess)
    }

    fun getViewersByAppointmentId(appt_id: Long, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>)->Unit):AccessReport?
    {
        context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                successCommand = viewerRepo.getViewersByAppointmentId(appt_id)
        ).execute(withAccess)
    }

    fun create(request: Create.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit):AccessReport?
    {
        context.require(
                requiredRoles = listOf(UserRole.Role.USER, UserRole.Role.RESEARCHER),
                successCommand = viewerFactory.create(request)
        ).execute(withAccess)

    }

}

