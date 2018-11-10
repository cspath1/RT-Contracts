package com.radiotelescope.repository.viewer

import com.radiotelescope.repository.appointment.Appointment
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface IViewerRepository : PagingAndSortingRepository<Viewer, Long>
{

    //@Query(value = , nativeQuery = true)
    //fun find
}
