package com.radiotelescope.controller.admin.appointment

import com.radiotelescope.contracts.appointment.ApproveDenyRequest
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.ses.SendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.service.ses.AwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * Rest Controller to handle listing appointment requests
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param awsSesSendService the [AwsSesSendService]
 * @param appointmentRepo the [IAppointmentRepository]
 * @param logger the [Logger] service
 */
@RestController
class AdminAppointmentApproveDenyRequestController (
        private val appointmentWrapper: UserAppointmentWrapper,
        private val appointmentRepo: IAppointmentRepository,
        private val awsSesSendService: AwsSesSendService,
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
    fun execute(@RequestParam("appointmentId") appointmentId: Long?,
                @RequestParam isApprove: Boolean?): Result {
        appointmentWrapper.approveDenyRequest(
                request = ApproveDenyRequest.Request(
                        appointmentId = appointmentId!!,
                        isApprove = isApprove!!
                )
        ) { it ->
            // If the command was a success
            it.success?.let { id ->
                // Create success logs
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Requested Appointment Review",
                                affectedRecordId = id
                        )
                )

                result = Result(data = id)

                sendEmail(
                        email = appointmentRepo.findById(id).get().user!!.email,
                        id = id,
                        isApprove = isApprove
                )
            }
            // Otherwise it was a failure
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Requested Appointment Reviewal",
                                affectedRecordId = null
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }
        ?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Requested Appointment Reviewal",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }
        return result
    }

    private fun sendEmail(email: String, id: Long, isApprove: Boolean) {
        val sendForm: SendForm
        if(isApprove) {
            sendForm = SendForm(
                    toAddresses = listOf(email),
                    fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                    subject = "Requested Observation Approved",
                    htmlBody = "<p>Your appointment with the id = $id has been approved " +
                            "and has now been scheduled for the allotted timeslot.</p>"
            )
        }
        else{
            sendForm = SendForm(
                    toAddresses = listOf(email),
                    fromAddress = "YCP Radio Telescope <cspath1@ycp.edu>",
                    subject = "Requested Observation Denied",
                    htmlBody = "<p>Your appointment with the id = $id has been denied.</p>"
            )
        }

        awsSesSendService.execute(sendForm)
    }
}