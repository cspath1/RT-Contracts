package com.radiotelescope.repository.appointment

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Spring Repository for the [Appointment] Entity
 */
@Repository
interface IAppointmentRepository : PagingAndSortingRepository<Appointment, Long> {
    /**
     * Spring Repository method that will return the number of milliseconds of
     * scheduled appointment time that a user has currently in the [Appointment]
     * table
     *
     * @param userId the User id
     * @return the appointment time in milliseconds
     */
    @Query(value = "SELECT SUM(" +
            "TIMESTAMPDIFF(MICROSECOND, " +
            "schedule_appointments.start_time, " +
            "schedule_appointments.end_time" +
            ") / 1000) " +
            "FROM (SELECT * " +
            "FROM appointment " +
            "WHERE user_id=?1 " +
            "AND end_time > CURRENT_TIMESTAMP " +
            "AND status = 'SCHEDULED') AS schedule_appointments",
            nativeQuery = true)
    fun findTotalScheduledAppointmentTimeForUser(userId: Long): Long?

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
            "AND status <> 'CANCELED'",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE user_id=?1 AND end_time > CURRENT_TIMESTAMP() " +
                    "AND status <> 'CANCELED'",
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
            "AND status <> 'CANCELED' ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE user_id=?1 " +
                    "AND end_time < CURRENT_TIMESTAMP " +
                    "AND status <> 'CANCELED'",
            nativeQuery = true)
    fun findPreviousAppointmentsByUser(userId: Long, pageable:Pageable): Page<Appointment>

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
            "AND status <> 'CANCELED' ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE telescope_id=?1 " +
                    "AND end_time > CURRENT_TIMESTAMP " +
                    "AND status <> 'CANCELED'" ,
        nativeQuery = true)
    fun retrieveFutureAppointmentsByTelescopeId(telescopeId: Long, pageable: Pageable): Page<Appointment>

    /**
     * Spring Repository method that will return all future [Appointment] records
     * for a User between specified start time and end time. Excludes canceled and requested appointments
     *
     * @param startTime the start time of when to start grabbing the appointment
     * @param endTime the end time of when to stop grabbing the appointment
     * @return a [List] of [Appointment]
     */
    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE ((start_time >=?1 AND start_time <=?2) " +
            "OR (end_time >=?1 AND end_time <=?2)" +
            "OR (start_time < ?1 AND end_time > ?2 ))" +
            "AND status <> 'CANCELED'" +
            "AND status <> 'REQUESTED' " +
            "AND telescope_id = ?3",
            nativeQuery = true)
    fun findAppointmentsBetweenDates(startTime: Date, endTime: Date, telescopeId: Long): List<Appointment>

    /**
     * Spring Repository method that will return all completed and public [Appointment]
     * records
     *
     * @param pageable the [Pageable] interface
     * @return a Page of [Appointment] records
     */
    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE status = 'COMPLETED' AND " +
            "public = 1",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE status = 'COMPLETED' AND " +
                    "public = 1",
            nativeQuery = true)
    fun findCompletedPublicAppointments(pageable: Pageable): Page<Appointment>

    /**
     * Spring Repository method that will return all request [Appointment] records
     *
     * @param pageable the [Pageable] interface
     * @return a [Page] of [Appointment]
     */
    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE status = 'REQUESTED'",
            countQuery = "SELECT COUNT(*) " +
                    "FROM appointment " +
                    "WHERE status = 'REQUESTED'",
            nativeQuery = true)
    fun findRequest(pageable: Pageable): Page<Appointment>

    /**
     * Spring Repository method that will return a list of appointments
     * that conflict with the potential appointment
     *
     * @param endTime the end time of potential appointment
     * @param startTime the start time of potential appointment
     * @param telescopeId the id of the pertaining telescope
     * @return a [List] of [Appointment]
     */
    @Query(value = "SELECT * " +
            "FROM appointment " +
            "WHERE start_time <= ?1 " +
            "AND end_time >= ?2 " +
            "AND telescope_id =?3 " +
            "AND status <> 'CANCELED' " +
            "AND status <> 'REQUESTED'",
            nativeQuery = true)
    fun findConflict(endTime: Date, startTime: Date, telescopeId: Long ):List<Appointment>

}