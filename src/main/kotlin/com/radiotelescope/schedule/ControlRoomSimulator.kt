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

@Component
class ControlRoomSimulator(
        private val appointmentRepo: IAppointmentRepository,
        private val rfDataRepo: IRFDataRepository,
        private val profile: Profile
) {
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
        }
    }

    fun handlePastAppointments(pastAppointments: List<Appointment>) {
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