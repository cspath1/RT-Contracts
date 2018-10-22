package com.radiotelescope.controller.model

import org.springframework.http.HttpStatus

/**
 * Class used to send information back to the client
 *
 * @property data the data returned on success
 * @property errors a map of any errors
 * @property status the associated [HttpStatus]
 */
class Result(
        var data: Any? = null,
        var errors: Map<String, Collection<String>>? = null,
        var status: HttpStatus? = null
) {
    init {
        if (status == null) status = if (errors == null) HttpStatus.OK
        else HttpStatus.BAD_REQUEST
    }
}