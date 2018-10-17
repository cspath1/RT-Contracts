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
import java.util.*

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

    override fun retrieveList(u: User, pageRequest:PageRequest):Command<Long,Multimap<ErrorTag, String>> {
        return RetrieveList(
                apptRepo = appointmentRepo,
                userId = u.id,
                userRepo = userRepo,
                pageRequest= pageRequest
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


    override fun update(appt_id: Long, newStartTime:Date, newEndTime:Date): Command<Long, Multimap<ErrorTag, String>>  {
        return Update(
                a_id = appt_id,
                apptRepo = appointmentRepo,
                newStartTime = newStartTime,
                newEndTime = newEndTime
        )
    }


    //Cancel appt
    override fun cancel(a: Appointment, apptRepo: IAppointmentRepository, apptId: Long): Command<Long, Multimap<ErrorTag, String>>  {
        return Cancel(
                apptId = apptId,
                apptRepo = apptRepo
        )
    }


    override fun retrieveByTelescopeId(id: Long, pageRequest: PageRequest, user_id: Long): Command<Long, Multimap<ErrorTag, String>>  {
        return RetrieveByTelescopeId(
                apptRepo = appointmentRepo,
               teleId = id,
                pageRequest = pageRequest,
                userRepo = userRepo,
                userId = user_id,
                teleRepo = telescopeRepo)
    }


    override fun getFutureAppointmentsForUser(userId: Long, pageRequest: PageRequest): Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        return ListFutureAppointmentByUser(
                userId = userId,
                pageRequest = pageRequest,
                apptRepo = appointmentRepo,
                userRepo = userRepo
        )
    }

}

