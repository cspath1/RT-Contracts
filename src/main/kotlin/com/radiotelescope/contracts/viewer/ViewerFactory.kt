package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.info.AppointmentInfo
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.viewer.Viewer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract factory interface with methods for all [Viewer] Command objects
 */
interface ViewerFactory {
    /**
     * Abstract command used to share a private appointment
     */
    fun sharePrivateAppointment(request: SharePrivateAppointment.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to list the shared appointments
     */
    fun listSharedAppointment(userId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to list the shared users
     */
    fun listSharedUser(appointmentId: Long, pageable: Pageable): Command<Page<UserInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to un-share private appointment
     */
    fun unsharePrivateAppointment(request: UnsharePrivateAppointment.Request): Command<Long, Multimap<ErrorTag, String>>
}