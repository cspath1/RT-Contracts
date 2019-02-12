package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository

/**
 * Base concrete implementation of the [ViewerFactory] interface
 *
 * @param viewerRepo the [IViewerRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 */
class BaseViewerFactory (
        private val viewerRepo: IViewerRepository,
        private val userRepo: IUserRepository,
        private val appointmentRepo: IAppointmentRepository
) : ViewerFactory{
    /**
     * Override of the [ViewerFactory.sharePrivateAppointment] method that will return a
     * [SharePrivateAppointment] command object
     *
     * @param request [SharePrivateAppointment.Request] object
     * @return a [SharePrivateAppointment] command object
     */
    override fun sharePrivateAppointment(request: SharePrivateAppointment.Request): Command<Long, Multimap<ErrorTag, String>> {
        return SharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )
    }
}