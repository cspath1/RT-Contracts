package com.radiotelescope.repository.viewer

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [Viewer] Entity
 */
@Repository
interface IViewerRepository : PagingAndSortingRepository<Viewer, Long> {

    /**
     * Spring Repository method that will grab a [List] of [Viewer]
     * with the given appointment Id parameter
     *
     * @param appointmentId the appointment Id
     * @return a [List] of [Viewer]
     */
    @Query(value = "SELECT * " +
            "FROM viewer " +
            "WHERE appointment_id = ?1",
            nativeQuery = true)
    fun findAllByAppointment(appointmentId: Long): List<Viewer>


    /**
     * Spring Repository method that will grab a [List] of [Viewer]
     * with the given user Id parameter
     *
     * @param userId the user Id
     * @return a [List] of [Viewer]
     */
    @Query(value = "SELECT * " +
            "FROM viewer " +
            "WHERE user_id = ?1",
            nativeQuery = true)
    fun findAllByUser(userId: Long): List<Viewer>

    /**
     * Spring Repository method that will check if the appointment
     * is shared with the user
     *
     * @param userId the User's Id
     * @param appointmentId the Appointment's Id
     * @return True if appointment is shared, false otherwise
     */
    @Query(value = "SELECT CASE WHEN EXISTS (" +
            "SELECT * FROM viewer " +
            "WHERE user_id=?1 AND appointment_id=?2 ) " +
            "THEN CAST(1 AS BIT) " +
            "ELSE CAST(0 AS BIT) END",
            nativeQuery = true)
    fun isAppointmentSharedWithUser(userId: Long, appointmentId: Long): Boolean

    /**
     * Spring Repository method that will grab a [List] of [Viewer]
     * with the given User's Id and Appointment's Id
     */
    @Query(value = "SELECT * FROM viewer " +
            "WHERE user_id=?1 AND appointment_id=?2",
            nativeQuery = true)
    fun findByUserAndAppointment(userId: Long, appointmentId: Long): List<Viewer>
}