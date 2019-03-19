package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.info.AppointmentInfo
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

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
     * @param request the [SharePrivateAppointment.Request] object
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

    /**
     * Override of the [ViewerFactory.listSharedAppointment] method that will return a
     * [ListSharedAppointment] command object
     *
     * @param userId the User's Id
     * @param pageable the [Pageable] object
     * @return a [ListSharedAppointment] command object
     */
    override fun listSharedAppointment(userId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return ListSharedAppointment(
                userId = userId,
                pageable = pageable,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )
    }

    /**
     * Override of the [ViewerFactory.listSharedUser] method that will return a
     * [ListSharedUser] command object
     *
     * @param appointmentId the Appointment's Id
     * @param pageable the [Pageable] object
     * @return a [ListSharedUser] command object
     */
    override fun listSharedUser(appointmentId: Long, pageable: Pageable): Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
        return ListSharedUser(
                appointmentId = appointmentId,
                pageable = pageable,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )
    }

    /**
     * Overrid of the [ViewerFactory.unSharePrivateAppointment] method that will
     * return a [UnsharePrivateAppointment] command object
     *
     * @param request the [UnsharePrivateAppointment.Request] object
     * @return a [UnsharePrivateAppointment] command object
     */
    override fun unsharePrivateAppointment(request: UnsharePrivateAppointment.Request): Command<Long, Multimap<ErrorTag, String>> {
        return UnsharePrivateAppointment(
                request = request,
                viewerRepo = viewerRepo,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )
    }
}