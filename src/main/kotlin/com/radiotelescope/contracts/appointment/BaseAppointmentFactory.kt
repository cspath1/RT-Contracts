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
   private var userRepo: IUserRepository //Would it make sense for the BaseApptFactory to have a userRepo?
        // (for the function to find appts by a User, fun findByUser(): List<Appointment>, in IUserRepository,
//findByUser is called in RetrieveList


): AppointmentFactory {

    //gets an appointment by id
    override fun retrieve(id: Long): Command<Long, Multimap<ErrorTag, String>> {
        return Retrieve(apptRepo.findById(id).get(), apptInfo , apptRepo, id)
    }

    override fun retrieveList(u: User):Command<Long,Multimap<ErrorTag, String>>
    {
        return RetrieveList(apptRepo, u, userRepo)
    }


    //implement
    /*
    override fun retrieve(request: Validate.Request): Command<Long, Multimap<ErrorTag, String>>  {


    }
    */

    //create an appointment
    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>  {

        return Create(request, apptRepo)
    }

    /*
    override fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>>  {
        return Update(request, apptRepo)
    }
    */

    //delete an appointment
    override fun delete(id: Long): Command<Long, Multimap<ErrorTag, String>>  {
        return Delete(apptRepo.findById(id).get(), apptRepo)
    }

}

