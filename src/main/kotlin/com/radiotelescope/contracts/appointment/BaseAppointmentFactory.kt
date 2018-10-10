package com.example.project.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.appointment.*
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

class BaseAppointmentFactory(
    private val apptRepo: IAppointmentRepository,
    private var apptInfo: AppointmentInfo,
    private var userRepo: IUserRepository
): AppointmentFactory {

    override fun retrieve(id: Long): Command<Long, Multimap<ErrorTag, String>> {
        return Retrieve(apptRepo.findById(id).get(), apptInfo, apptRepo, id)
    }

    override fun retrieveList(u: User):Command<Long,Multimap<ErrorTag, String>>
    {
        return RetrieveList(apptRepo, u.id, userRepo)
    }

    //Create appt
    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>  {

        return Create(request, apptRepo)
    }

    override fun update(appt_id: Long): Command<Long, Multimap<ErrorTag, String>>  {
        return Update(appt_id, apptRepo)
    }

    //Delete appt
    override fun delete(id: Long): Command<Long, Multimap<ErrorTag, String>>  {
        return Delete(id, apptRepo)
    }
}

