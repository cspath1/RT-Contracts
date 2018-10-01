package com.radiotelescope.contracts

/**
 * Data class used to respond to the client in the event of either a
 * success or a failure
 */
data class SimpleResult<out S, out E>(val success: S?, val error: E?)