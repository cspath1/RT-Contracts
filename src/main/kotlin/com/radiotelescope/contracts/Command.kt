package com.radiotelescope.contracts

import com.radiotelescope.security.AccessReport

/**
 * Command Interface responsible for handling all interactions
 * with the Spring Data JPA layer of the application.
 *
 * The S type parameter refers to whatever the success data type is
 * The E type parameter refers to whatever the error data type is
 */
interface Command<out S, out E> {
    /**
     * Abstract execute method responsible for executing the client-side request
     */
    fun execute(): SimpleResult<S, E>
}

/**
 * Command Interface responsible for handling all user authentication.
 *
 * The S type parameter refers to whatever the success data type is
 * The E type parameter refers to whatever the error data type is
 *
 * @param withAccess an anonymous function that takes a [SimpleResult] as a parameter and returns [Unit]
 */
interface SecuredAction<out S, out E> {
    fun execute(withAccess: (result: SimpleResult<S, E>)->Unit): AccessReport?
}