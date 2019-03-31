package com.radiotelescope.contracts.rfdata

import com.radiotelescope.repository.rfdata.RFData
import java.util.*

/**
 * Data class representing a read-only model of the
 * RFData Entity.
 *
 * @param id the RFData's id
 * @param appointmentId the RFData's appointment id
 * @param intensity the RFData's intensity reading
 * @param timeCaptured the time the RFData was captured
 */
data class RFDataInfo(
        val id: Long,
        val appointmentId: Long,
        val intensity: Long,
        val timeCaptured: Date
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
            intensity = rfData.getIntensity()!!,
            timeCaptured = rfData.getTimeCaptured()!!
    )
}