package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.Appointment
import java.util.regex.Pattern
//View class for Appointment Entity

enum class status {

    REQUESTED,
    SCHEDULED,
    INPROGRESS,
    COMPLETED,
    CANCELLED

}

//

enum class receiver {
""

}

data class AppointmentInfo

//date is supposed to be of type "LocalDate"?

(var id:Long, var type:Int, var state:Int, var assocUserId:Int, var startTime:String, var endTime:String,
var status:status, var date:String, var telescopeId:Long, var celestialBodyId:Long, var latitude:Long, var longitude:Long,
 var receiver:receiver, var public: boolean)
{

    constructor(appointment: Appointment) : this
    (
        id = appointment.id,
        type = appointment.type,
        state = appointment.state, assocUserId = appointment.assocUserId,

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








