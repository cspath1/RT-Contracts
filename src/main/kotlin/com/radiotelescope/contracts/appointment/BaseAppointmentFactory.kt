package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.data.domain.Page
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
     * Override of the [AppointmentFactory.userCompletedList] method that will return a [UserCompletedList] command object
     *
     * @param userId the [User] id
     * @param pageable the [Pageable] interface
     * @return a [UserCompletedList] command object
     */

    override fun userCompletedList(userId: Long, pageable: Pageable): Command<Page<AppointmentInfo>,Multimap<ErrorTag, String>> {
        return UserCompletedList(
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
     * @param request the [Update.Request]
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
     * @param pageable the [Pageable] interface
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
     * Override of the [AppointmentFactory.userFutureList] method that will
     * return a [UserFutureList] command object
     *
     * @param userId the User id
     * @param pageable the [Pageable] interface
     * @return the [UserFutureList] command object
     */
    override fun userFutureList(userId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return UserFutureList(
                userId = userId,
                pageable = pageable,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.retrieveFutureAppointmentsByTelescopeId] method
     * that will return a [RetrieveFutureAppointmentsByTelescopeId] command object
     *
     * @param telescopeId the Telescope id
     * @param pageable the [Pageable] interface
     * @return the [RetrieveFutureAppointmentsByTelescopeId] command object
     */
    override fun retrieveFutureAppointmentsByTelescopeId(telescopeId: Long, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return RetrieveFutureAppointmentsByTelescopeId(
                appointmentRepo = appointmentRepo,
                telescopeId = telescopeId,
                pageable = pageable,
                telescopeRepo = telescopeRepo
        )
    }
}

