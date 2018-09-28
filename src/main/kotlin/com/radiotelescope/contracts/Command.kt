package com.radiotelescope.contracts

import com.radiotelescope.security.AccessReport

/**
 * Command Interface responsible for handling all interactions
 * with the Spring Data JPA layer of the application
 */
interface Command<out S, out E> {
    /**
     * Abstract execute method responsible for executing the client-side request
     */
    fun execute(): SimpleResult<S, E>
}

/**
 * Command Interface responsible for handling all user authentication
 * @param withAccess an anonymous function that takes a [SimpleResult] as a parameter and returns [Unit]
 */
interface SecuredAction<out S, out E> {
    fun execute(withAccess: (result: SimpleResult<S, E>)->Unit): AccessReport?
}