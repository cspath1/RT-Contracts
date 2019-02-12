package com.radiotelescope.contracts.viewer

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

/**
 * Abstract factory interface with methods for all [Viewer] Command objects
 */
interface ViewerFactory {
    /**
     * Abstract command used to share a private appointment
     */
    fun sharePrivateAppointment(request: SharePrivateAppointment.Request): Command<Long, Multimap<ErrorTag, String>>
}