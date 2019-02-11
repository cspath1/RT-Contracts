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

}