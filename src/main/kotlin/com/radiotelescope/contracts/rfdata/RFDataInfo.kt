package com.radiotelescope.contracts.rfdata

import com.radiotelescope.repository.rfdata.RFData

/**
 * Data class representing a read-only model of the
 * RFData Entity.
 *
 * @param id the RFData's id
 * @param appointmentId the RFData's appointment id
 * @param intensity the RFData's intensity reading
 */
data class RFDataInfo(
        val id: Long,
        val appointmentId: Long,
        val intensity: Long
) {
    /**
     * Secondary constructor that takes an RFData Entity
     * object to set all fields
     *
     * @param rfData the RFData
     */
    constructor(rfData: RFData) : this(
            id = rfData.getId(),
            appointmentId = rfData.getAppointment()!!.id,
            intensity = rfData.getIntensity()!!
    )
}