package com.radiotelescope.controller.admin.notification

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.appointment.create.CelestialBodyAppointmentCreateForm
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.UserContext
import com.radiotelescope.service.sns.ErrorTag
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.service.sns.IAwsSnsSendService
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminTopicNotificationController (
        private val context: UserContext,
        private val roleRepo: IUserRoleRepository,
        private val awsSesSendService: IAwsSesSendService,
        private val awsSnsSendService: IAwsSnsSendService,
        logger: Logger
) : BaseRestController(logger) {
    @PostMapping(value = ["/api/notification/topic"])
    fun execute(@RequestParam(value = "topic", required = false, defaultValue = "") topic: String,
                @RequestParam(value = "message", required = true) message: String
    ) : Result {
        // Test if the user is an admin
        val isAdmin = (roleRepo.findAllApprovedRolesByUserId(context.currentUserId()!!).find { role -> UserRole.Role.ADMIN == role.role } != null)
        if (isAdmin) {
            sendSms(
                    topic = topic,
                    message = message
            )?.let { errors ->
                result =  if (errors.isEmpty) Result() else Result(errors = errors.toStringMap(), status = HttpStatus.BAD_REQUEST)
            }
        } else {
            result = Result(status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    private fun sendEmail(email: String, form: CelestialBodyAppointmentCreateForm) {
        val sendForm = SesSendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                subject = "Celestial Body Appointment Created",
                htmlBody = "<p>Your celestial body appointment has been scheduled to start at ${form.startTime} " +
                        "and end at ${form.endTime}.</p>"
        )
        awsSesSendService.execute(sendForm)
    }

    private fun sendSms(topic: String, message: String) :  HashMultimap<ErrorTag, String>?{
        val sendForm = SnsSendForm(
                toNumber = null,
                topic = topic,
                message = message
        )
        return awsSnsSendService.execute(sendForm)
    }
}