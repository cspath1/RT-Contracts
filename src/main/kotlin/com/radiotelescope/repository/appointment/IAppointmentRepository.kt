package com.radiotelescope.repository.appointment;



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

    fun findByUserId(userId: Long): List<Appointment>


}