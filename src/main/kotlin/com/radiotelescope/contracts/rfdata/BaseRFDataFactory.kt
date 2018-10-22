package com.radiotelescope.contracts.rfdata

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository

/**
 * Base concrete implementation of the [RFDataFactory] interface
 *
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param rfDataRepo the [IRFDataRepository] interface
 */
class BaseRFDataFactory(
        private val appointmentRepo: IAppointmentRepository,
        private val rfDataRepo: IRFDataRepository
) : RFDataFactory {
    /**
     * Override of the [RFDataFactory.retrieveAppointmentData] method that will return
     * a [RetrieveAppointmentData] command object
     *
     * @param appointmentId the Appointment Id
     * @return a [RetrieveAppointmentData] command object
     */
    override fun retrieveAppointmentData(appointmentId: Long): Command<List<RFDataInfo>, Multimap<ErrorTag, String>> {
        return RetrieveAppointmentData(
                appointmentId = appointmentId,
                appointmentRepo = appointmentRepo,
                rfDataRepo = rfDataRepo
        )
    }
}