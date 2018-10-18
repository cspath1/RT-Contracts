package com.radiotelescope.repository.rfdata

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository Interface for the [RFData] Entity
 */
@Repository
interface IRFDataRepository : PagingAndSortingRepository<RFData, Long> {
    /**
     * Spring Repository method that will return all [RFData] records
     * for an appointment
     *
     * @param appointmentId the Appointment id
     * @return a [List] of [RFData]
     */
    @Query(value = "SELECT * " +
            "FROM rf_data " +
            "WHERE appointment_id=?1",
            nativeQuery = true)
    fun findRFDataForAppointment(appointmentId: Long): List<RFData>
}