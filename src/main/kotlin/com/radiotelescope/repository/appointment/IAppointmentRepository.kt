package com.radiotelescope.repository.appointment;

import com.radiotelescope.repository.user.User
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/*
Spring Repository Interface for the Appointment Entity
 */
@Repository
interface IAppointmentRepository : PagingAndSortingRepository<Appointment, Long> {
/*
find appointment by userId

 */


    fun findByUser(user: User): List<Appointment>

   //May need to implement
 //   fun findByUserId(uId: Long): List<Appointment>

    //needs to be implemented
  //  fun findByUserFirstAndLastName(): List<Appointment>

    //needs to be implemented
  //  fun existsByUserFirstAndLastName(): Boolean

    //needs to be implemented. (if one appt exists, by a Us
    fun existsByUserId(): Boolean

    //a specific appointment with a particular appointment ID (findById in-built)
 //   fun findByApptId(apptId : Long): Appointment




    override fun delete(a: Appointment): Unit
    {
        a.status = Appointment.Status.Canceled;
        //anything else?

    }



}