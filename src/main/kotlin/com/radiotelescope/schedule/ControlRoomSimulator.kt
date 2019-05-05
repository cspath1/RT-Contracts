package com.radiotelescope.schedule

import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import com.radiotelescope.repository.rfdata.RFData
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import kotlin.random.Random

/**
 * Spring Component that acts as a simulator for the control room, handling
 * all of the different stages of conducting an appointment. This will only
 * be ran on the development server for now
 *
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param rfDataRepo the [IRFDataRepository] interface
 * @param profile the application's [Profile]
 */
@Component
class ControlRoomSimulator(
        private val appointmentRepo: IAppointmentRepository,
        private val rfDataRepo: IRFDataRepository,
        private val profile: Profile
) {
    /**
     * Handles the following four scenarios:
     *
     * 1.) Fast-forwarding all appointments that are scheduled, but the end time has already passed
     * 2.) Beginning all appointments that are scheduled, but now have a start time in the past
     * 3.) Marking all in progress appointments as completed if the end time is now in the past
     * 4.) Adding a new RF Data point for in progress appointments that still have end times in the future
     *
     * This method will run every minute, allowing for a simulated RF Data reading of once per minute for
     * all in progress appointments.
     */
    @Scheduled(cron = Settings.EVERY_MINUTE)
    fun execute() {
        if (profile == Profile.DEV) {
            val scheduledAppointments = appointmentRepo.findAllScheduledAppointments()

            // Handle all scheduled appointments that have "ended" in the past
            // that may not have been accounted for
            // Create a copy of the scheduled appointments list, filtering out all appointments
            // that are in the future
            val pastAppointments = scheduledAppointments.toMutableList().filter {
                // Filters out items that have an end time in the future
                it.endTime < Date()
            }
            handlePastAppointments(pastAppointments)

            // Handle "starting" any appointments that have a start time before now
            // that are not already marked as in progress
            val startedAppointments = scheduledAppointments.toMutableList().filter {
                it.startTime < Date()
            }
            handleStartingAppointments(startedAppointments)

            // Handle all in progress appointments. This will require two different things
            // 1.) Marking all appointments as completed if the end time has passed
            // OR
            // 2.) Adding a new RF Data reading to the appointment
            val inProgressAppointments = appointmentRepo.findAllInProgressAppointments()

            // Mark all appointments as completed first, since we do not want to add a new
            // RF Data point for these appointments
            val completedAppointments = inProgressAppointments.toMutableList().filter {
                it.endTime <= Date()
            }
            handleCompletedAppointments(completedAppointments)

            // Next, handle all appointments that are still in progress
            val stillInProgressAppointments = inProgressAppointments.toMutableList().filter {
                it.endTime > Date()
            }
            handleInProgressAppointments(stillInProgressAppointments)
        }
    }

    /**
     * In the event of the server going down, or handling a backlog of appointments,
     * this will handle fast-forwarding appointments that have already ended, but are
     * still marked '[Appointment.Status.SCHEDULED] so they are completed and contain
     * RF Data
     *
     * @param pastAppointments all appointments that have ended but are still marked Scheduled
     */
    private fun handlePastAppointments(pastAppointments: List<Appointment>) {
        pastAppointments.forEach {
            val minute = 1000 * 60 // number of milliseconds in a minute
            val numberOfMinutesInAppointment = (it.endTime.time - it.startTime.time) / minute

            // Note: 'until' excludes the upper bound (i.e. < numberOfMinutesInAppointment)
            for (i in 0 until numberOfMinutesInAppointment) {
                // First reading will be one minute after start (i + 1)
                val time = Date(it.startTime.time + ((i + 1) * minute))
                generateRFDataValue(
                        appointment = it,
                        timeCaptured = time
                )
            }

            it.status = Appointment.Status.COMPLETED
            appointmentRepo.save(it)
        }
    }

    /**
     * Handles "starting" all appointments that are marked scheduled and have
     * a start time in the past. Sets the status for each to [Appointment.Status.IN_PROGRESS]
     *
     * @param startedAppointment all appointments with a start time in the past
     */
    fun handleStartingAppointments(startedAppointment: List<Appointment>) {
        startedAppointment.forEach {
            // For each appointment, do not add any RF Data
            // rather, just set the status to 'in progress'
            // Data will be added next time this is ran
            it.status = Appointment.Status.IN_PROGRESS
            appointmentRepo.save(it)
        }
    }

    /**
     * Handles "completing" all appointments that are in progress and now have a
     * end time in the past. Sets the status for each to [Appointment.Status.COMPLETED]
     *
     * @param completedAppointments all in progress appointments that are now completed
     */
    fun handleCompletedAppointments(completedAppointments: List<Appointment>) {
        completedAppointments.forEach {
            // For each appointment, mark it as completed
            it.status = Appointment.Status.COMPLETED
            appointmentRepo.save(it)
        }
    }

    /**
     * Adds a new RF Data point to all appointments that are still in progress
     *
     * @param inProgressAppointments all in progress appointments that are not yet completed
     */
    fun handleInProgressAppointments(inProgressAppointments: List<Appointment>) {
        inProgressAppointments.forEach {
            // For each appointment, generate a new RF Data
            generateRFDataValue(
                    appointment = it,
                    timeCaptured = Date()
            )
        }
    }

    /**
     * Creates a single RF Data point for the appointment
     *
     * @param appointment the Appointment
     * @param timeCaptured the time the data was "captured"
     */
    private fun generateRFDataValue(
            appointment: Appointment,
            timeCaptured: Date
    ) {
        val data = RFData()

        data.appointment = appointment
        data.timeCaptured = timeCaptured
        data.intensity = Random.nextInt(0, 4096).toLong()

        rfDataRepo.save(data)
    }
}