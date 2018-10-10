package com.radiotelescope.repository.appointment;

import com.radiotelescope.repository.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/*
Spring Repository Interface for the Appointment Entity
 */
@Repository
interface IAppointmentRepository : PagingAndSortingRepository<Appointment, Long> {
//Find an appointment by user id (AFTER the current time)
    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE user_id=?1 AND end_time > CURRENT_TIMESTAMP()",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE user_id=?1 AND end_time > CURRENT_TIMESTAMP() \\n#pageable\\n",
            nativeQuery = true)
    fun findFutureAppointmentsByUser(userId: Long, pageable: Pageable): Page<Appointment>

    @Query(value = "select * from appointment where user_id=?1 AND end_time < CURRENT_TIMESTAMP", countQuery = "select count(*) from appointment where user_id=?1 and end_time < current_timestamp \\n#pageable\\n", nativeQuery = true)
    fun findPreviousAppointmentsByUser(userId: Long, pageable:Pageable): Page<Appointment>

    //Do we need to ensure that the change is reflected in the IAppointmentRepository?
    @Query(value = "update a appointment a set status = 'Canceled' where id = ?1")
    fun delete(userId: Long):Appointment

    /*
    override fun delete(a: Appointment): Unit
    {
        a.status = Appointment.Status.Canceled;
    }
    */
    //update (edit start or end time)
    @Query(value = "update a appointment a set start_time = ?1, end_time = ?2 where id = ?3 ")
    fun updateSingleAppointmentTimes(starttime:Long, endtime: Long, id:Long):Appointment



    @Query(value ="select * from appointment where telescope_id = ?1")
    fun retrieveAppointmentsByTelescopeId(tele_id: Long, pageable:Pageable): Page<Appointment>

}