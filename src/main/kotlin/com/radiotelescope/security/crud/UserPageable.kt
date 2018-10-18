package com.radiotelescope.security.crud

import com.radiotelescope.security.AccessReport

interface UserPageable<in REQUEST, out RESULT> {
    fun pageable(request: REQUEST, withAccess: (result: RESULT)->Unit): AccessReport?

}