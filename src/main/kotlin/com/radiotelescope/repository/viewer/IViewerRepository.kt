package com.radiotelescope.repository.viewer

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface IViewerRepository : PagingAndSortingRepository<Viewer, Long>
{
    @Query(value = "select *" +
            " from viewer" +
            " where sharing_user_id =?1",
            nativeQuery = true)
    fun getViewersOfAppointmentBySharingUserId(sharing_user_id:Long, pageable: Pageable): Page<Viewer>
}