package com.radiotelescope.contracts.appointment.factory

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.*
import com.radiotelescope.contracts.appointment.info.AppointmentInfo
import com.radiotelescope.contracts.appointment.update.CoordinateAppointmentUpdate
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.appointment.SearchCriteria
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation of the [AppointmentFactory] interface
 *
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param telescopeRepo the [ITelescopeRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
abstract class BaseAppointmentFactory(
        private val appointmentRepo: IAppointmentRepository,
        private val userRepo: IUserRepository,
        private val telescopeRepo: ITelescopeRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
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

    /**
     * Override of the [AppointmentFactory.listBetweenDates] method
     * that will return a [ListBetweenDates] command object
     *
     * @param request the [ListBetweenDates.Request] object
     * @return a [ListBetweenDates] command object
     */
    override fun listBetweenDates(request: ListBetweenDates.Request): Command<List<AppointmentInfo>, Multimap<ErrorTag, String>> {
       return ListBetweenDates(
               request = request,
               appointmentRepo = appointmentRepo,
               telescopeRepo = telescopeRepo

       )
    }

    /**
     * Override of the [AppointmentFactory.makePublic] method
     * that will return a [MakePublic] command object
     *
     * @param appointmentId the Appointment's id
     * @return the [MakePublic] command object
     */
    override fun makePublic(appointmentId: Long): Command<Long, Multimap<ErrorTag, String>> {
        return MakePublic(
                appointmentId = appointmentId,
                appointmentRepo = appointmentRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.publicCompletedAppointments] method
     * that will return public, completed appointments
     *
     * @param pageable the Pageable interface
     * @return a [PublicCompletedAppointments] command object
     */
    override fun publicCompletedAppointments(pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return PublicCompletedAppointments(
                pageable = pageable,
                appointmentRepo = appointmentRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.requestedList] method that will return a [RequestedList]
     * command object
     *
     * @param pageable the [Pageable] object that has the page number and page size
     * @return a [RequestedList] command
     */
    override fun requestedList(pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return RequestedList(
                pageable = pageable,
                userRepo = userRepo,
                appointmentRepo = appointmentRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.approveDenyRequest] method that will return a [RequestedList]
     * command object
     *
     * @param request the [ApproveDenyRequest.Request] object
     * @return a [Command] object
     */
    override fun approveDenyRequest(request: ApproveDenyRequest.Request): Command<Long, Multimap<ErrorTag, String>> {
        return ApproveDenyRequest(
                request = request,
                appointmentRepo = appointmentRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.userAvailableTime] method that will return a [UserAvailableTime]
     * command object
     *
     * @param userId the User's Id
     * @return a [Command] object
     */
    override fun userAvailableTime(userId: Long): Command<Long, Multimap<ErrorTag, String>> {
        return UserAvailableTime(
                userId = userId,
                appointmentRepo = appointmentRepo,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [AppointmentFactory.search] method that will return a [Search] command object
     *
     * @return a [Search] command object
     */
    override fun search(searchCriteria: List<SearchCriteria>, pageable: Pageable): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                appointmentRepo = appointmentRepo
        )
    }
}