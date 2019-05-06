package com.radiotelescope.repository.appointment

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Spring Repository for the [Appointment] Entity
 */
@Repository
interface IAppointmentRepository : PagingAndSortingRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    /**
     * Spring Repository method that will return the number of milliseconds of
     * scheduled appointment time that a user has currently in the [Appointment]
     * table. Note that this method will NOT include any secondary appointments
     * that have been scheduled by admins.
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
            "AND status = 'SCHEDULED' " +
            "AND priority = 'PRIMARY') AS schedule_appointments",
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
            "AND telescope_id = ?3 " +
            "AND status <> 'CANCELED' " +
            "AND status <> 'REQUESTED' " +
            "AND priority = ?4",
            nativeQuery = true)
    fun findConflict(endTime: Date, startTime: Date, telescopeId: Long, priority: String):List<Appointment>

    /**
     * Spring Repository method that will return a list of appointments
     * that was shared with a user
     *
     * @param userId the User's Id
     * @return a [Page] of [Appointment] records
     */
    @Query(value = "SELECT * FROM appointment " +
            "WHERE id IN (SELECT appointment_id FROM viewer WHERE user_id=?1)",
            countQuery = "SELECT COUNT(*) FROM appointment " +
                    "WHERE id IN (SELECT appointment_id FROM viewer WHERE user_id=?1)",
            nativeQuery = true)
    fun findSharedAppointmentsByUser(userId: Long, pageable: Pageable): Page<Appointment>

    /**
     * Currently used to find the first in progress appointment for a specific telescope
     * to determine if it is a free control appointment (meaning it cannot be overriden)
     *
     * @param status the [Appointment.Status]
     * @param telescopeId the Radio Telescope id
     * @return an Appointment or null
     */
    fun findFirstByStatusAndTelescopeId(status: Appointment.Status, telescopeId: Long): Appointment?

    /**
     * Finds all scheduled appointments. Currently used so the control room simulator
     * can simulate conducting an appointment.
     *
     * @return a [List] of [Appointment] objects
     */
    @Query(value = "SELECT * FROM appointment " +
            "WHERE status = 'SCHEDULED'",
            nativeQuery = true)
    fun findAllScheduledAppointments(): List<Appointment>

    /**
     * Finds all in progress appointments. Currently used so the control room simulator
     * can simulate conducting an appointment
     *
     * @return a [List] of [Appointment] objects
     */
    @Query(value = "SELECT * FROM appointment " +
            "WHERE status = 'IN_PROGRESS'",
            nativeQuery = true)
    fun findAllInProgressAppointments(): List<Appointment>
}