package com.radiotelescope.repository.viewer

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Repository for Viewers.
 *
 */

@Repository
interface IViewerRepository : PagingAndSortingRepository<Viewer, Long>
{
    /**
     * Method to get all Viewers of a User (i.e. all the people who can see certain appointments by that User), by User Id.
     * @param sharing_user_id [Long] the ID of the user who is sharing his or her appointment
     * @param pageable [Pageable] the Page of viewers requested
     * @return   Page<Viewer>
     */
    @Query(value = "select *" +
            " from viewer" +
            " where sharing_user_id =?1",
            nativeQuery = true)
    fun getViewersOfAppointmentBySharingUserId(sharing_user_id:Long, pageable: Pageable): Page<Viewer>




    /**
     * Method to get viewers of an Appointment by Appointment Id.
     * @param sharing_appointment_id [Long] the ID of the appointment to be viewed by a Viewer
     * @param pageable [Pageable] the Page of viewers requested
     * @return Page<Viewer>
     */
    @Query(value = "select *" +
            " from viewer" +
            " where shared_appointment_id = ?1", nativeQuery = true)
    fun getViewersByAppointmentId(shared_appointment_id:Long, pageable:Pageable): Page<Viewer>
}
