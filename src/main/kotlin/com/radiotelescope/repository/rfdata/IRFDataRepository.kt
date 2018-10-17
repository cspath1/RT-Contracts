package com.radiotelescope.repository.rfdata

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository Interface for the RFData Entity
 */
@Repository
interface IRFDataRepository : PagingAndSortingRepository<RFData, Long> {
    @Query(value = "SELECT * " +
            "FROM rf_data " +
            "WHERE appointment_id=?1",
            nativeQuery = true)
    fun findRFDataForAppointment(appointmentId: Long): List<RFData>
}