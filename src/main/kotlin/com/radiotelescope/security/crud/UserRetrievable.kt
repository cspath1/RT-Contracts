package com.radiotelescope.security.crud

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.security.AccessReport

/**
 * Interface used to override a retrieve-based crud operation Command
 * and add Spring Security authentication to the Command
 *
 * Example: [UserUserWrapper.retrieve]
 */
interface UserRetrievable<in REQUEST, out RESULT> {
    fun retrieve(request: REQUEST, withAccess: (result: RESULT)->Unit): AccessReport?
}