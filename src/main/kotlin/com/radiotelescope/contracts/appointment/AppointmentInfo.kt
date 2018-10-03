package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.user.User
import java.util.*
//View class for Appointment Entity




 data class AppointmentInfo(

   var appt:Appointment,
    var user:User, //we need user id, lastname and firstname
  //  var orientation:Orientation, //later to implement Orientation entity
  //  var celestialBody:CelestialBody, //same
    var typeI: String,
    var startTimeI: Date,
    var endTimeI: Date,
    var telescopeIdI: Long,
    var celestialBodyIdI: Long,
    var receiverI: String,
    var isPublicI: Boolean,
    var dateI: Date,
    var assocUserIdI:Int,
    var uFirstName: String,
    var uLastName: String,
    var apptidI: Long,
    var statusI: Appointment.Status,
    var stateI: Int
)

 {

   init {

       typeI = appt.type;
       startTimeI = appt.startTime;
       endTimeI = appt.endTime;
       telescopeIdI = appt.telescopeId;
       celestialBodyIdI = appt.celestialBodyId;
       receiverI = appt.receiver;
       isPublicI = appt.isPublic;
       dateI = appt.date;
       assocUserIdI = appt.assocUserId;
       uFirstName = user.firstName;
       uLastName = user.lastName;
       apptidI = appt.id;
       statusI = appt.status;
       stateI = appt.state;

   }


    //Get 2ndary constructor working
/*
      constructor(a: Appointment): this( u = a.user, typeI = a.type, startTimeI = a.startTime, endTimeI = a.endTime, telescopeIdI = a.telescopeId, celestialBodyIdI = a.celestialBodyId,
            receiverI = a.receiver, isPublicI = a.isPublic, dateI = a.date, assocUserIdI = a.assocUserId, uFirstName = a.user.firstName, uLastName = a.user.lastName,
            apptidI = a.id, statusI = a.status, stateI = a.state
            )
         */



    }








