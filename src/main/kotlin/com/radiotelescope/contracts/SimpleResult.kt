package com.radiotelescope.contracts

data class SimpleResult<out S, out E>(val success: S?, val error: E?)