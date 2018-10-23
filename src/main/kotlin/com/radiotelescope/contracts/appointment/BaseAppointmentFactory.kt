package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation of the [AppointmentFactory] interface
 *
 * @param appointmentRepo the [IAppointmentRepository]
 * @param userRepo the [IUserRepository]
 * @param telescopeRepo the [ITelescopeRepository]
 */
class BaseAppointmentFactory(
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val telescopeRepo: ITelescopeRepository
) : AppointmentFactory {
    /**
     * Override of the [AppointmentFactory.retrieve] method that will return a [Retrieve]
     * command object
     *
     * @param id the [Appointment] id
     * @return a [Retrieve] command object
     */
    override fun retrieve(id: Long): Command<AppointmentInfo, Multimap<ErrorTag, String>> {
        return Retrieve(
                appointmentId = id,
                appointmentRepo = appointmentRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.pastAppointmentListForUser] method that will return a [PastAppointmentListForUser] command object
     *
     * @param userId the [User] id
     * @param pageRequest the [PageRequest]
     * @return a [PastAppointmentListForUser] command object
     */

    override fun pastAppointmentListForUser(userId: Long, pageable: Pageable): Command<Page<AppointmentInfo>,Multimap<ErrorTag, String>> {
        return PastAppointmentListForUser(
                appointmentRepo = appointmentRepo,
                userId = userId,
                userRepo = userRepo,
                pageable = pageable
        )
    }

    /**
     * Override of the [AppointmentFactory.create] method that will return a [Create]
     * command object
     *
     * @param request the [Create.Request] object
     * @return a [Create] command
     */
    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>  {
        return Create(
                request = request,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                telescopeRepo = telescopeRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.update] method that will return a [Update] command object
     *
     * @param appt_id of type [Long], the id of the appointment which you want to update
     * @param newStartTime of type [Date] , the new start Time you wish to give to this appointment
     * @param newEndTime of type [Date], the new endTime you wish to give to this appointment
     * @param teleId of type [Long], the new telescopeId you wish to give to this appointment
     * @return a [Update] command object
     */
    override fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>>  {
        return Update(
                request = request,
                appointmentRepo = appointmentRepo,
                telescopeRepo = telescopeRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.cancel] method that will return a [Cancel] command object
     *
     * @param appointmentId the Appointment id
     * @return a [Cancel] command object
     */
    override fun cancel(appointmentId: Long): Command<Long, Multimap<ErrorTag, String>>  {
        return Cancel(
                appointmentId = appointmentId,
                appointmentRepo = appointmentRepo
        )
    }
    /**
     * Override of the [AppointmentFactory.retrieveByTelescopeId] method that will return a [RetrieveByTelescopeId] command object
     *
     * @param telescopeId the Telescope id
     * @param pageRequest the [PageRequest] object
     * @return a [RetrieveByTelescopeId] command object
     */
    override fun retrieveByTelescopeId(telescopeId: Long, pageable: Pageable): Command <Page<AppointmentInfo>, Multimap<ErrorTag, String>>  {
        return RetrieveByTelescopeId(
                appointmentRepo = appointmentRepo,
                telescopeId = telescopeId,
                pageable = pageable,
                telescopeRepo = telescopeRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.getFutureAppointmentsForUser] method that will
     * return a [ListFutureAppointmentByUser] command object
     *
     * @param userId the User id
     * @param pageable the [Pageable] interface
     * @return the [ListFutureAppointmentByUser] command object
     */
    override fun getFutureAppointmentsForUser(userId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return ListFutureAppointmentByUser(
                userId = userId,
                pageable = pageable,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo
        )
    }

    override fun retrieveFutureAppointmentsByTelescopeId(telescopeId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return RetrieveFutureAppointmentsByTelescopeId(
                appointmentRepo = appointmentRepo,
                telescopeId = telescopeId,
                pageable = pageable,
                telescopeRepo = telescopeRepo
        )
    }
}

