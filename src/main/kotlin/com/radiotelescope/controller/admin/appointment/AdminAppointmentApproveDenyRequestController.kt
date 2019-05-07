package com.radiotelescope.controller.admin.appointment

import com.radiotelescope.contracts.appointment.ApproveDenyRequest
import com.radiotelescope.contracts.appointment.wrapper.UserAutoAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.ApproveDenyForm
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.AwsSesSendService
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * Rest Controller to handle listing appointment requests
 *
 * @param autoAppointmentWrapper the [UserAutoAppointmentWrapper]
 * @param awsSesSendService the [AwsSesSendService]
 * @param appointmentRepo the [IAppointmentRepository]
 * @param logger the [Logger] service
 */
@RestController
class AdminAppointmentApproveDenyRequestController (
        @Qualifier(value = "coordinateAppointmentWrapper")
        private val autoAppointmentWrapper: UserAutoAppointmentWrapper,
        private val appointmentRepo: IAppointmentRepository,
        private val awsSesSendService: IAwsSesSendService,
        logger: Logger
) : BaseRestController(logger){
    /**
     * Execute method that is in charge of taking the incoming
     * email and pass it to the command to create reset password token
     *
     * @param isApprove the approve/deny of an appointment request
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PutMapping(value = ["/api/appointments/{appointmentId}/validate"])
    fun execute(@PathVariable("appointmentId") appointmentId: Long?,
                @RequestParam("isApprove") isApprove: Boolean?)
            : Result {
        val form = ApproveDenyForm(
                appointmentId = appointmentId,
                isApprove = isApprove
        )
        form.validateRequest()?.let{ errors ->
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Requested Appointment Review",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )

            result = Result(errors = errors.toStringMap())
        } ?: let{
            autoAppointmentWrapper.approveDenyRequest(
                    request = ApproveDenyRequest.Request(
                            appointmentId = appointmentId!!,
                            isApprove = isApprove!!
                    )
            ) { response ->
                // If the command was a success
                response.success?.let { id ->
                    // Create success logs
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Requested Appointment Review",
                                    affectedRecordId = id,
                                    status = HttpStatus.OK.value()
                            )
                    )

                    result = Result(data = id)

                    sendEmail(
                            email = appointmentRepo.findById(id).get().user.email,
                            id = id,
                            isApprove = isApprove
                    )
                }
                // Otherwise it was a failure
                response.error?.let { errors ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Requested Appointment Review",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            } ?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Requested Appointment Review",
                                affectedRecordId = null,
                                status = HttpStatus.FORBIDDEN.value()
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }
        return result
    }

    /**
     * Method used to send out an email to the appointment owner, letting them know
     * whether their requested appointment was approved or denied.
     *
     * @param email the user's email
     * @param id the Appointment id
     * @param isApprove whether the request was approved or not
     */
    private fun sendEmail(email: String, id: Long, isApprove: Boolean) {
        val sendForm: SendForm
        if(isApprove) {
            sendForm = SendForm(
                    toAddresses = listOf(email),
                    fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                    subject = "Requested Observation Approved",
                    htmlBody = "<p>Your appointment with the id = $id has been approved " +
                            "and has now been scheduled for the allotted time slot.</p>"
            )
        } else {
            sendForm = SendForm(
                    toAddresses = listOf(email),
                    fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                    subject = "Requested Observation Denied",
                    htmlBody = "<p>Your appointment with the id = $id has been denied.</p>"
            )
        }

        awsSesSendService.execute(sendForm)
    }

}