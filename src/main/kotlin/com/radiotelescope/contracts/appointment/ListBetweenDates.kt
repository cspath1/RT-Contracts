package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.info.AppointmentInfo
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.toAppointmentInfoList
import java.util.*

/**
 * Override of the [Command] interface used to retrieve appointments
 * between start time and end time.
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 */
class ListBetweenDates(
        private val request: ListBetweenDates.Request,
        private val appointmentRepo: IAppointmentRepository,
        private val radioTelescopeRepo: IRadioTelescopeRepository
) : Command<List<AppointmentInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes it will create a [List] of [AppointmentInfo] objects and
     * return this in the [SimpleResult.success] value.
     *
     * If validation fails, it will will return the errors in a [SimpleResult.error]
     * value.
     */
    override fun execute(): SimpleResult<List<AppointmentInfo>, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if(!errors.isEmpty){
            return SimpleResult(null, errors)
        }

        val list = appointmentRepo.findAppointmentsBetweenDates(
                startTime = request.startTime,
                endTime = request.endTime,
                telescopeId = request.telescopeId
        )

        val infoList = list.toAppointmentInfoList()
        return SimpleResult(infoList, null)
    }

    /**
     * Method responsible for constraint checking and validations that
     * ensures the endTime is greater than startTime
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            // Check to see if endTime is less than or equal to startTime
            if (endTime <= startTime)
                errors.put(ErrorTag.END_TIME, "End time cannot be less than or equal to start time")
            if (!radioTelescopeRepo.existsById(telescopeId))
                errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId could not be found")
        }
        return errors
    }

    /**
     * Data class containing all fields necessary for listing appointments between dates
     */
    data class Request(
            var telescopeId: Long,
            var startTime: Date,
            var endTime: Date
    )
}