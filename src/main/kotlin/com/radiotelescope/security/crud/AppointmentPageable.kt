package com.radiotelescope.security.crud

import com.radiotelescope.security.AccessReport

interface AppointmentPageable<in REQUEST, out RESULT> {
    fun pageableAppointments(request: REQUEST, withAccess: (result: RESULT)->Unit): Unit

}