package com.radiotelescope.repository.appointment

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
/*
find appointment by userId

 */

    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE user_id=?1 AND end_time > CURRENT_TIMESTAMP()",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE user_id=?1 AND end_time > CURRENT_TIMESTAMP() \\n#pageable\\n",
            nativeQuery = true)
    fun findFutureAppointmentsByUser(userId: Long, pageable: Pageable): Page<Appointment>

    fun findByUser(user: User): List<Appointment>

    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE user_id=?1 AND end_time < CURRENT_TIMESTAMP()",
            countQuery = "SELECT COUNT(*) " +
                "FROM appointment " +
                "WHERE user_id=?1 AND end_time < CURRENT_TIMESTAMP() \\n#pageable\\n",
            nativeQuery = true)
    fun findPastAppointmentsByUser(userId: Long, pageable: Pageable) : Page<Appointment>

    override fun delete(a: Appointment)
    {
        a.status = Appointment.Status.Canceled
        //anything else?

    }
}