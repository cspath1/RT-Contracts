package com.radiotelescope.repository.appointment;



import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository



/*

Spring Repository Interface for the Appointment Entity

 */
@Repository
interface AppointmentRepository : PagingAndSortingRepository<Appointment, Long> {
/*
find appointment by userId and by appointmentID


 */

    fun findApptByApptID(): Appointment?

    fun findApptByUserID(): Appointment?


}