package com.radiotelescope.contracts.rfdata

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

/**
 * Abstract factory interface with methods for all RFData command objects
 */
interface RFDataFactory {
    /**
     * Abstract command used to retrieve appointment data for a completed
     * observation
     *
     * @param appointmentId the Appointment id
     * @return a [Command] object
     */
    fun retrieveAppointmentData(appointmentId: Long): Command<List<RFDataInfo>, Multimap<ErrorTag, String>>
}