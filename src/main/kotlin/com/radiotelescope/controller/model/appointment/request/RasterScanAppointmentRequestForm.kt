package com.radiotelescope.controller.model.appointment.request

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.appointment.request.RasterScanAppointmentRequest
import com.radiotelescope.contracts.coordinate.CoordinateRequest
import com.radiotelescope.controller.model.coordinate.CoordinateForm
import com.radiotelescope.isNotEmpty
import com.radiotelescope.repository.appointment.Appointment
import java.util.*

/**
 * Request form that takes nullable versions of the [RasterScanAppointmentRequest.Request] object.
 * It is in charge of making sure these values are not null before adapting the form into a
 * [RasterScanAppointmentRequest.Request] object
 *
 * @param userId the User id
 * @param startTime the Appointment start time
 * @param endTime the Appointment end time
 * @param telescopeId the Telescope id
 * @param isPublic whether the Appointment is public or not
 * @param priority the Appointment priority
 * @param coordinates the List of [CoordinateForm] objects
 */
data class RasterScanAppointmentRequestForm(
        override val userId: Long?,
        override val startTime: Date?,
        override val endTime: Date?,
        override val telescopeId: Long?,
        override val isPublic: Boolean?,
        override val priority: Appointment.Priority?,
        val coordinates: List<CoordinateForm>?
) : RequestForm<RasterScanAppointmentRequest.Request>() {
    /**
     * Override of the [RequestForm.toRequest] method that adapts the
     * form into a [RasterScanAppointmentRequest.Request] object
     *
     * @return the [RasterScanAppointmentRequest.Request] object
     */
    override fun toRequest(): RasterScanAppointmentRequest.Request {
        val coordinatesRequests: MutableList<CoordinateRequest> = mutableListOf()
        coordinates!!.forEach { coordinatesRequests.add(it.toRequest()) }

        return RasterScanAppointmentRequest.Request(
                userId = userId!!,
                startTime = startTime!!,
                endTime = endTime!!,
                telescopeId = telescopeId!!,
                isPublic = isPublic!!,
                priority = priority!!,
                coordinates = coordinatesRequests
        )
    }

    /**
     * Method that makes sure all required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (userId == null)
            errors.put(ErrorTag.USER_ID, "Invalid user id")
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