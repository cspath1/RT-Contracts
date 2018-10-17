package com.radiotelescope.security.crud

import com.radiotelescope.security.AccessReport

interface AppointmentRetrievable<in REQUEST, out RESULT> {
    fun retrieve(request: REQUEST, withAccess: (result: RESULT)->Unit): AccessReport?
}