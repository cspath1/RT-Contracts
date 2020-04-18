package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.wrapper.UserAutoAppointmentWrapper
import com.radiotelescope.contracts.appointment.Cancel
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AccessReport
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.service.sns.IAwsSnsService
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller used to cancel an appointment
 *
 * Note that for actions done to the Appointment Table that
 * are not creates, the specific [UserAutoAppointmentWrapper]
 * does not matter.
 *
 * @param autoAppointmentWrapper the [UserAutoAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentCancelController(
        @Qualifier(value = "coordinateAppointmentWrapper")
        private val autoAppointmentWrapper: UserAutoAppointmentWrapper,
        private val userRepo: IUserRepository,
        private val appointmentRepo: IAppointmentRepository,
        private val awsSesSendService: IAwsSesSendService,
        private val awsSnsService: IAwsSnsService,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the appointmentId [PathVariable]
     * and executing the [UserAutoAppointmentWrapper.cancel] method. If this method returns
     * an [AccessReport], this means the user did not pass authentication and it should
     * respond with errors.
     *
     * Otherwise, this means the [Cancel] command was executed, and the controller should
     * respond to the client based on if the command was a success or not
     */
    @PutMapping(value = ["/api/appointments/{appointmentId}/cancel"])
    fun execute(@PathVariable("appointmentId") appointmentId: Long): Result {
        autoAppointmentWrapper.cancel(appointmentId) {
            // If the command was a success
            it.success?.let { id ->
                // Create success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Cancellation",
                                affectedRecordId = id,
                                status = HttpStatus.OK.value()
                        )
                )

                val theUser = userRepo.findById(appointmentRepo.findById(appointmentId).get().user.id).get()

                // Send an email or an SMS depending on the user's notification type
                if (theUser.notificationType == User.NotificationType.EMAIL ||
                        theUser.notificationType == User.NotificationType.ALL) {
                    sendEmail(theUser.email)
                }

                if (theUser.notificationType == User.NotificationType.SMS ||
                        theUser.notificationType == User.NotificationType.ALL) {
                    sendSms(theUser.phoneNumber!!)
                }

                result = Result(data = id)
            }
            // Otherwise it was a failure
            it.error?.let { error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Cancellation",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = error.toStringMap()
                )

                result = Result(errors = error.toStringMap())
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Appointment Cancellation",
                            affectedRecordId = null,
                            status = if (it.missingRoles != null) HttpStatus.FORBIDDEN.value() else HttpStatus.NOT_FOUND.value()
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

    private fun sendEmail(email: String) {
        val sendForm = SesSendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                subject = "Appointment Cancelled",
                htmlBody = "<p>Your appointment with the YCAS Radio Telescope has been cancelled.</p>"
        )
        awsSesSendService.execute(sendForm)
    }

    private fun sendSms(phoneNumber: String) {
        val sendForm = SnsSendForm(
                toNumber = phoneNumber,
                topic = null,
                message = "Your appointment with the YCAS Radio Telescope has been cancelled."
        )
        awsSnsService.send(sendForm)
    }
}