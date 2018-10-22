package com.radiotelescope.security.crud

import com.radiotelescope.security.AccessReport

interface UserUpdatable<in REQUEST, out RESULT> {
    fun update(request: REQUEST, withAccess: (result: RESULT)->Unit): AccessReport?
}