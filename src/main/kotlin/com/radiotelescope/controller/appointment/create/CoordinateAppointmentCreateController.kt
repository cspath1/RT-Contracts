package com.radiotelescope.controller.appointment.create

import com.radiotelescope.contracts.appointment.wrapper.UserAutoAppointmentWrapper
import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.create.CoordinateAppointmentCreateForm
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.service.sns.IAwsSnsSendService
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle Point Appointment Creation
 *
 * @param autoAppointmentWrapper the [UserAutoAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class CoordinateAppointmentCreateController(
        @Qualifier(value = "coordinateAppointmentWrapper")
        private val autoAppointmentWrapper: UserAutoAppointmentWrapper,
        private val userRepo: IUserRepository,
        private val awsSesSendService: IAwsSesSendService,
        private val awsSnsSendService: IAwsSnsSendService,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [CoordinateAppointmentCreateForm]
     * into a [CoordinateAppointmentCreate.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserAutoAppointmentWrapper.create] method.
     * If this method returns an [AccessReport], this means the user did not pass
     * authentication and the method will respond with errors.
     *
     * Otherwise, the [CoordinateAppointmentCreate] command was executed, and the controller will check
     * whether this command was a success or not, responding appropriately.
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/appointments/schedule/coordinate"])
    fun execute(@RequestBody form: CoordinateAppointmentCreateForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Point Appointment Creation",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            val theUser = userRepo.findById(form.userId!!).get()

            // Send an email or an SMS depending on the user's notification type
            if (theUser.notificationType == User.NotificationType.EMAIL ||
                    theUser.notificationType == User.NotificationType.ALL) {
                sendEmail(theUser.email, form)
            }

            if (theUser.notificationType == User.NotificationType.SMS ||
                    theUser.notificationType == User.NotificationType.ALL) {
                sendSms(theUser.phoneNumber!!, form)
            }

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise, execute the wrapper command
        let {
            autoAppointmentWrapper.create(
                    request = form.toRequest()
            ) { response ->
                // If the command called was a success
                response.success?.let { data ->
                    // Create success logs
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Point Appointment Creation",
                                    affectedRecordId = data,
                                    status = HttpStatus.OK.value()
                            )
                    )

                    result = Result(data = data)
                }
                // Otherwise, it was an error
                response.error?.let { errors ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Point Appointment Creation",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            }?.let { report ->
                // If we get here, this means the User did not pass authentication
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Point Appointment Creation",
                                affectedRecordId = null,
                                status = HttpStatus.FORBIDDEN.value()
                        ),
                        errors = report.toStringMap()
                )

                result = Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }

    private fun sendEmail(email: String, form: CoordinateAppointmentCreateForm) {
        val sendForm = SesSendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                subject = "Celestial Body Appointment Created",
                htmlBody = "<p>Your coordinate appointment has been scheduled to start at ${form.startTime} " +
                        "and end at ${form.endTime}.</p>"
        )
        awsSesSendService.execute(sendForm)
    }

    private fun sendSms(phoneNumber: String, form: CoordinateAppointmentCreateForm) {
        val sendForm = SnsSendForm(
                toNumber = phoneNumber,
                topic = null,
                message = "Your coordinate appointment has been scheduled to start at ${form.startTime} " +
                        "and end at ${form.endTime}."
        )
        awsSnsSendService.execute(sendForm)
    }
}