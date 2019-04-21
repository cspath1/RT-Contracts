package com.radiotelescope.repository.subscribedAppointment

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ISubscribedAppointmentRepository : PagingAndSortingRepository<subscribedAppointment, Long> {

}