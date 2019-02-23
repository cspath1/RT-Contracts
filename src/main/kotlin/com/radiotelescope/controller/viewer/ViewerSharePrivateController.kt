package com.radiotelescope.controller.viewer

import com.radiotelescope.contracts.viewer.SharePrivateAppointment
import com.radiotelescope.contracts.viewer.UserViewerWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.DateTimeFormats
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * REST Controller to handle Sharing Private Appointment
 *
 * @param viewerWrapper the [UserViewerWrapper]
 * @param awsSesSendService the [IAwsSesSendService]
 * @param appointmentRepo the [IAppointmentRepository]
 * @param logger the [Logger] service
 */
@RestController
class ViewerSharePrivateController(
        private val viewerWrapper: UserViewerWrapper,
        private val awsSesSendService: IAwsSesSendService,
        private val appointmentRepo: IAppointmentRepository,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of sharing a private appointment by executing
     * [UserViewerWrapper.sharePrivateAppointment] method
     *
     * If it was successful, it will notify the user that an appointment has been shared
     * with them
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/appointments/{appointmentId}/viewers"])
    fun execute(@PathVariable("appointmentId") appointmentId: Long,
                @RequestParam("userId") email: String): Result {
        val request = SharePrivateAppointment.Request(
                email = email,
                appointmentId = appointmentId
        )
        viewerWrapper.sharePrivateAppointment(request) {
            // If the command was a success
            it.success?.let { id ->
                logger.createSuccessLog(
                        info = Logger.createInfo(Log.AffectedTable.VIEWER,
                                action = "Share Private Appointment",
                                affectedRecordId = id
                        )
                )
                result = Result(data = id)

                sendEmail(email, appointmentId)
            }
            // If the command was a failure
            it.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.VIEWER,
                                action = "Share Private Appointment",
                                affectedRecordId = null
                        ),
                        errors = errors.toStringMap()
                )
                result = Result(errors = errors.toStringMap())
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.RF_DATA,
                            action = "Share Private Appointment",
                            affectedRecordId = null
                    ),
                    errors = if (it.missingRoles != null) it.toStringMap() else it.invalidResourceId!!
            )

            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            result = if (it.missingRoles == null) {
                Result(errors = it.invalidResourceId!!, status = HttpStatus.NOT_FOUND)
            }
            // user did not have access to the resource
            else {
                Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }


    private fun sendEmail(email: String, appointmentId: Long) {
        val appointment = appointmentRepo.findById(appointmentId).get()

        var htmlBody = "<p>${appointment.user.firstName} ${appointment.user.lastName} has shared one of their private" +
                "observations with you.</p>"

        htmlBody += when {
            appointment.status == Appointment.Status.SCHEDULED -> {
                val appointmentStart = DateTimeFormats.dateTimeFormat.format(LocalDateTime.ofInstant(appointment.startTime.toInstant(), ZoneId.systemDefault()))

                "<p>This observation is scheduled for $appointmentStart.</p>"
            }
            appointment.status == Appointment.Status.IN_PROGRESS -> "<p>This observation is currently in progress! You can see the data that has been " +
                    "captured thus far</p>"
            else -> {
                "<p>This observation has already been conducted, so you will now be able to see all of the data captured.</p>"
            }
        }

        val sendForm = SendForm(
                toAddresses = listOf(email),
                fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                subject = "Share Private Appointment",
                htmlBody = htmlBody
        )

        awsSesSendService.execute(sendForm)
    }
}