package com.radiotelescope.controller.appointment.request

import com.radiotelescope.contracts.appointment.wrapper.UserAutoAppointmentWrapper
import com.radiotelescope.contracts.appointment.request.RasterScanAppointmentRequest
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.request.RasterScanAppointmentRequestForm
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AccessReport
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle Raster Scan Appointment requests
 *
 * @param autoAppointmentWrapper the [UserAutoAppointmentWrapper]
 * @param awsSesSendService the [IAwsSesSendService]
 * @param userRepo the [IUserRepository]
 * @param logger the [Logger] service
 */
@RestController
class RasterScanAppointmentRequestController(
        @Qualifier(value = "rasterScanAppointmentWrapper")
        private val autoAppointmentWrapper: UserAutoAppointmentWrapper,
        private val awsSesSendService: IAwsSesSendService,
        private val userRepo: IUserRepository,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [RasterScanAppointmentRequestForm]
     * into a [RasterScanAppointmentRequest] after ensuring no fields are null. If any are,
     * it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserAutoAppointmentWrapper.request] method.
     *
     * If this method returns an [AccessReport], this means the user was not authorized,
     * and the controller will return errors. Otherwise, the [RasterScanAppointmentRequest]
     * command was executed, and the controller will respond based on if the commmand was
     * a success or not.
     */
    @PostMapping(value = ["/api/appointments/request/raster-scan"])
    fun execute(@RequestBody form: RasterScanAppointmentRequestForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Raster Scan Appointment Request",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise, execute the wrapper command
        let {
            autoAppointmentWrapper.request(
                    request = form.toRequest()
            ) { response ->
                // If the command called was a success
                response.success?.let { data ->
                    // Create success logs
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Raster Scan Appointment Request",
                                    affectedRecordId = data,
                                    status = HttpStatus.OK.value()
                            )
                    )

                    sendEmail(userRepo.findAllAdminEmail())

                    result = Result(data = data)
                }
                // Otherwise, it was an error
                response.error?.let { errors ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Raster Scan Appointment Request",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            }
        }?.let { report ->
            // If we get here, this means the User did not pass authentication
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Raster Scan Appointment Request",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = report.toStringMap()
            )

            result = Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    /**
     * Sends an email out to admins letting them know there is a new requested appointment
     * that requires their attention.
     *
     * @param emails a list of admin email addresses
     */
    private fun sendEmail(emails: List<String>) {
        val sendForm = SesSendForm(
                toAddresses = emails,
                fromAddress = "YCAS Radio Telescope <info@astroyork.com>",
                subject = "Appointment Request",
                htmlBody = "<p>A new observation has been requested by a user at their " +
                        "allotted quota and requires your approval.</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}