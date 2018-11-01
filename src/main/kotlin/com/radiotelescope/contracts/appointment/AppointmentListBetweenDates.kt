package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toAppointmentInfoList
import java.util.*

/**
 * Override of the [Command] interface used to retrieve appointments
 * between start time and end time.
 *
 * @param startTime the start time of when to grab appointments
 * @param endTime the end time of when to grab the appointments
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class AppointmentListBetweenDates(
        private val startTime: Date,
        private val endTime: Date,
        private val appointmentRepo : IAppointmentRepository,
        private val userRepo : IUserRepository
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
                startTime = startTime,
                endTime = endTime
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

        // Check to see if endTime is less than or equal to startTime
        if(endTime <= startTime)
            errors.put(ErrorTag.END_TIME, "End time cannot be less than or equal to start time")

        return errors
    }
}