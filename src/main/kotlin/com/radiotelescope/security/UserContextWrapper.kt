package com.radiotelescope.security

/**
 * Interface used in tandem with Spring Security to allow user role
 * validations
 */
interface UserContextWrapper<out T> {
    fun factory(userPreconditionFailure: UserPreconditionFailure): T
}
