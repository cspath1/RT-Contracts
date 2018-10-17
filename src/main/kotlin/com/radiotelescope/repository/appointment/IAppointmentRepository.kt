package com.radiotelescope.repository.appointment

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
            countQuery = "SELECT count(*) FROM appointment",
            nativeQuery = true)
    fun findFutureAppointmentsByUser(userId: Long, pageable: Pageable): Page<Appointment>

    @Query(value = "select * from appointment where user_id=?1 AND end_time < CURRENT_TIMESTAMP", countQuery = "select count(*) from appointment where user_id=?1 and end_time < current_timestamp \\n#pageable\\n", nativeQuery = true)
    fun findPreviousAppointmentsByUser(userId: Long, pageable:Pageable): Page<Appointment>

    @Query(value ="select * from appointment where telescope_id = ?1", nativeQuery = true)
    fun retrieveAppointmentsByTelescopeId(tele_id: Long, pageable:Pageable): Page<Appointment>
}