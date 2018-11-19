package com.radiotelescope.contracts.appointment

import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

/**
 * Override of the [Command] interface method used for Appointment creation
 *
 * @param request the [Request] object
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param telescopeRepo the [ITelescopeRepository] interface
 */
class Create(
    private val request: Request,
    private val appointmentRepo: IAppointmentRepository,
    private val userRepo: IUserRepository,
    private val userRoleRepo: IUserRoleRepository,
    private val telescopeRepo: ITelescopeRepository,
    private val coordinateRepo: ICoordinateRepository
) : Command<Long, Multimap<ErrorTag,String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [Appointment] object and
     * return the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theAppointment = request.toEntity()

            val theCoordinate = request.toCoordinate()
            coordinateRepo.save(theCoordinate)

            theAppointment.user = userRepo.findById(request.userId).get()
            theAppointment.coordinate = theCoordinate

            appointmentRepo.save(theAppointment)
            return SimpleResult(theAppointment.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * appointment create request. It will ensure that both the user and telescope
     * id exists and that the appointment's end time is not before its start time.
     * It also ensures that the start time is not before the current date
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        var errors = HashMultimap.create<ErrorTag,String>()
        with(request) {
            if (!userRepo.existsById(userId)) {
                errors.put(ErrorTag.USER_ID, "User #$userId could not be found")
                return errors
            }
            if (!telescopeRepo.existsById(telescopeId)) {
                errors.put(ErrorTag.TELESCOPE_ID, "Telescope #$telescopeId could not be found")
                return errors
            }
            if (startTime.after(endTime))
                errors.put(ErrorTag.END_TIME, "Start time must be before end time")
            if (startTime.before(Date()))
                errors.put(ErrorTag.START_TIME, "Start time must be after the current time" )
            if (isOverlap())
                errors.put(ErrorTag.OVERLAP, "Appointment time is conflicted with another appointment")
            if (rightAscension > 360 || rightAscension < 0)
                errors.put(ErrorTag.RIGHT_ASCENSION, "Right Ascension must be between 0 - 360")
            if (declination > 90 || declination < 0)
                errors.put(ErrorTag.DECLINATION, "Declination must be between 0 - 90")

            if (!errors.isEmpty)
                return errors

            errors = validateAvailableAllottedTime()

        }

      return if (errors.isEmpty) null else errors
    }

    /**
     * Method responsible for checking if a user has enough available time
     * to schedule the new observation, as well as having a membership role
     */
    private fun validateAvailableAllottedTime(): HashMultimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            val newAppointmentTime = endTime.time - startTime.time
            val totalTime = appointmentRepo.findTotalScheduledAppointmentTimeForUser(userId) ?: 0
            val theUserRole = userRoleRepo.findMembershipRoleByUserId(userId)

            if (theUserRole == null) {
                errors.put(ErrorTag.CATEGORY_OF_SERVICE, "User's Category of Service has not yet been approved")
                return errors
            }

            when (theUserRole.role) {
                // Guest -> 5 hours
                UserRole.Role.GUEST -> {
                    if ((totalTime + newAppointmentTime) > Appointment.GUEST_APPOINTMENT_TIME_CAP)
                        errors.put(ErrorTag.ALLOTTED_TIME, "You may only have up to 5 hours of observation time as a Guest")
                }
                // Everyone else -> 50 hours
                else -> {
                    if ((totalTime + newAppointmentTime) > Appointment.OTHER_USERS_APPOINTMENT_TIME_CAP)
                        errors.put(ErrorTag.ALLOTTED_TIME, "Max allotted observation time is 50 hours at any given time")
                }
            }
        }

        return errors
    }

    /**
     * Method responsible for check if the requested appointment
     * conflict with the one that are already scheduled
     */
    private fun isOverlap(): Boolean {
        var isOverlap = false
        val listAppts = appointmentRepo.findConflict(
                endTime = request.endTime,
                startTime = request.startTime,
                telescopeId = request.telescopeId
        )

        if (!listAppts.isEmpty()) {
            isOverlap = true
        }

        return isOverlap
    }

    /**
     * Data class containing all fields necessary for appointment creation. Implements
     * the [BaseCreateRequest] interface.
     */
    data class Request(
            val userId: Long,
            val startTime: Date,
            val endTime: Date,
            val telescopeId: Long,
            val isPublic: Boolean,
            val rightAscension: Double,
            val declination: Double
    ) : BaseCreateRequest<Appointment> {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] method that
         * returns an Appointment object
         */
        override fun toEntity(): Appointment {
            return Appointment(
                    startTime = startTime,
                    endTime = endTime,
                    telescopeId = telescopeId,
                    isPublic = isPublic
            )
        }

        fun toCoordinate(): Coordinate {
            return Coordinate(
                    rightAscension = rightAscension,
                    declination = declination
            )
        }
    }
}