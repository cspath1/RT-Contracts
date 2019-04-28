package com.radiotelescope.controller.model.appointment.update

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.update.RasterScanAppointmentUpdate
import com.radiotelescope.contracts.coordinate.CoordinateRequest
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.controller.model.coordinate.CoordinateForm
import com.radiotelescope.isNotEmpty
import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Update form that takes nullable versions of the [RasterScanAppointmentUpdate.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * to a [RasterScanAppointmentUpdate.Request] object
 *
 * @param startTime the Appointment's new start time
 * @param endTime the Appointment's new end time
 * @param telescopeId the Appointment's new telescope id
 * @param isPublic whether the Appointment is to be public or not
 * @param priority the Appointment's new priority
 * @param coordinates the List of [CoordinateForm] objects
 */
data class RasterScanAppointmentUpdateForm(
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        override val priority: Appointment.Priority?,
        val coordinates: List<CoordinateForm>?
) : UpdateForm<RasterScanAppointmentUpdate.Request>() {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [RasterScanAppointmentUpdate.Request] object
     *
     * @return the [RasterScanAppointmentUpdate.Request] object
     */
    override fun toRequest(): RasterScanAppointmentUpdate.Request {
        val coordinateRequests: MutableList<CoordinateRequest> = mutableListOf()
        coordinates!!.forEach { coordinateRequests.add(it.toRequest()) }

        return RasterScanAppointmentUpdate.Request(
                id = -1L,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                priority = priority!!,
                coordinates = coordinateRequests
        )
    }

    /**
     * Method that makes sure all required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (startTime == null)
            errors.put(ErrorTag.START_TIME, "Required field")
        if (endTime == null)
            errors.put(ErrorTag.END_TIME, "Required field")
        if (telescopeId == null)
            errors.put(ErrorTag.TELESCOPE_ID, "Required field")
        if (isPublic == null)
            errors.put(ErrorTag.PUBLIC, "Required field")
        if (priority == null)
            errors.put(ErrorTag.PRIORITY, "Required field")
        if (coordinates == null) {
            errors.put(ErrorTag.COORDINATES, "Two Coordinates are required")
        } else if (coordinates.size != 2)
            errors.put(ErrorTag.COORDINATES, "Two Coordinates are required")

        if (errors.isNotEmpty())
            return errors

        coordinates!!.forEach { form ->
            form.validateRequest()?.let { return it }
        }

        // If none of the coordinate forms fail validation, we can return null
        return null
    }
}
