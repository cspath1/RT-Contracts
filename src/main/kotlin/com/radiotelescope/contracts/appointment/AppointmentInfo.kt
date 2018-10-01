package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.Appointment
import java.util.regex.Pattern
//View class for Appointment Entity

//

data class AppointmentInfo

//date is supposed to be of type "LocalDate"?
(
        var id: Long,
        var type: String,
        var assocUserId:Int,
        var startTime: Date,
        var endTime: Date,
        var status: Status,
        var telescopeId: Long,
        var celestialBodyId: Long,
        var latitude: Long,
        var longitude: Long,
        var receiver: String,
        var public: boolean
)
{

    constructor(appointment: Appointment) : this
    (
        id = appointment.id,
        type = appointment.type,
        state = appointment.state,
        assocUserId = appointment.assocUserId,
        startTime = appointment.startTime,
        endTime = appointment.endTime,
        status = appointment.status,
        date = appointment.date,
        telescopeId = appointment.telescopeId,
        celestialBodyId = appointment.celestialBodyId,
        latitude = appointment.latitude,
        longitude = appointment.longitude,
        receiver = appointment.receiver,
        public = appointment.public



    )



    //check, e.g. email validation
//regex







    }








