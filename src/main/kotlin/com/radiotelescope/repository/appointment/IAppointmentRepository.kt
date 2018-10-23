package com.radiotelescope.repository.appointment

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [Appointment] Entity
 */
@Repository
interface IAppointmentRepository : PagingAndSortingRepository<Appointment, Long> {
    /**
     * Spring Repository method that will return all future [Appointment] records
     * for a User. Excludes canceled appointments
     *
     * @param userId the User id
     * @param pageable the [Pageable] interface
     * @return a [Page] of [Appointment]
     */
    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE user_id=?1 AND end_time > CURRENT_TIMESTAMP() " +
            "AND status <> 'Canceled'",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE user_id=?1 AND end_time > CURRENT_TIMESTAMP() " +
                    "AND status <> 'Canceled'",
            nativeQuery = true)
    fun findFutureAppointmentsByUser(userId: Long, pageable: Pageable): Page<Appointment>

    /**
     * Spring Repository method that will return all completed appointments
     * for a User.
     *
     * @param userId the User id
     * @param pageable the [Pageable] interface
     * @return a [Page] of [Appointment] objects
     */
    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE user_id=?1 " +
            "AND end_time < CURRENT_TIMESTAMP " +
            "AND status <> 'Canceled' ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE user_id=?1 " +
                    "AND end_time < CURRENT_TIMESTAMP " +
                    "AND status <> 'Canceled'",
            nativeQuery = true)
    fun findPreviousAppointmentsByUser(userId: Long, pageable:Pageable): Page<Appointment>

    /**
     * Spring repository method to retrieve appointments for a give telescope id
     *
     * @param telescopeId the TelescopeId
     * @param pageable the [Pageable] interface
     * @return a [Page] of [Appointment] objects
     */
    @Query(value ="SELECT * " +
            "FROM appointment " +
            "WHERE telescope_id = ?1",
            nativeQuery = true)
    fun retrieveAppointmentsByTelescopeId(telescopeId: Long, pageable:Pageable): Page<Appointment>

    /**
     * Spring repository method that retrieves all future appointments by telescope id
     *
     * @param telescopeId the Telescope id
     * @param pageable the [Pageable] interface
     * @return a [Page] of [Appointment] objects
     */
    @Query(value= "SELECT * " +
            "FROM appointment " +
            "WHERE telescope_id = ?1 " +
            "AND end_time > CURRENT_TIMESTAMP " +
            "AND status <> 'Canceled' ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE telescope_id=?1 " +
                    "AND end_time > CURRENT_TIMESTAMP " +
                    "AND status <> 'Canceled'" ,
        nativeQuery = true)
    fun retrieveFutureAppointmentsByTelescopeId(telescopeId: Long, pageable:Pageable):Page<Appointment>
}