package com.radiotelescope.schedule

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.subscribedAppointment.ISubscribedAppointmentRepository
import com.radiotelescope.repository.subscribedAppointment.subscribedAppointment
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class PublishNotification(
        private val logRepo: ILogRepository,
        private val subscribedAppointmentRepo: ISubscribedAppointmentRepository,
        private val appointmentRepo: IAppointmentRepository
) {
    @Scheduled(cron = Settings.EVERY_MINUTE)
    fun execute(){
        subscribedAppointmentRepo.findAll().forEach{
            if (appointmentRepo.findById(it.appointmentId).get().startTime.before(Date()) && it.status == subscribedAppointment.Status.SCHEDULED){
                val builder = AmazonSNSClientBuilder.standard().build()
                val topicARN = builder.createTopic("UserTopic" + it.userId).topicArn

                builder.publish(topicARN, "Your appointment " + it.appointmentId + "is about to begin.")
                it.status = subscribedAppointment.Status.STARTED
                subscribedAppointmentRepo.save(it)
                builder.shutdown()
                createLogs(it)
            }
            else if(appointmentRepo.findById(it.appointmentId).get().endTime.before(Date()) && it.status == subscribedAppointment.Status.STARTED){
                val builder = AmazonSNSClientBuilder.standard().build()
                val topicARN = builder.createTopic("UserTopic" + it.userId).topicArn

                builder.publish(topicARN, "Your appointment " + it.appointmentId + "has finished.")
                subscribedAppointmentRepo.deleteById(it.id)
                builder.shutdown()
                createLogs(it)
            }
        }
    }

    private fun createLogs(subscribedAppointment: subscribedAppointment) {
        val notifySubscribedAppointmentLog = Log(
                affectedTable = Log.AffectedTable.SUBSCRIBED_APPOINTMENT,
                action = "Notify User of Subscribed Appointment",
                timestamp = Date(),
                affectedRecordId = subscribedAppointment.id
        )

        logRepo.save(notifySubscribedAppointmentLog)
    }

}