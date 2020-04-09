package com.radiotelescope.controller.admin.notification

import com.google.common.collect.HashMultimap
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.sns.SnsSendForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.UserContext
import com.radiotelescope.service.sns.ErrorTag
import com.radiotelescope.service.sns.IAwsSnsSendService
import com.radiotelescope.toStringMap
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller to handle Admin broadcast notifications
 *
 * @param context the [UserContext]
 * @param roleRepo the [IUserRoleRepository]
 * @param logger the [Logger] service
 */
@RestController
class AdminTopicNotificationController (
        private val context: UserContext,
        private val roleRepo: IUserRoleRepository,
        private val awsSnsSendService: IAwsSnsSendService,
        logger: Logger
) : BaseRestController(logger) {
    @Value("\${amazon.aws.sns.default-topic}")
    lateinit var defaultSendTopic: String

    @PostMapping(value = ["/api/notification/topic"])
    fun execute(@RequestParam(value = "topic", required = false) topic: String?,
                @RequestParam(value = "message", required = true) message: String
    ) : Result {
        // If the topic is left blank, use the default topic
        var sendTopic = topic
        if(topic.isNullOrBlank()) {
            sendTopic = defaultSendTopic
        }

        // Test if the user is an admin
        val isAdmin = if(context.currentUserId() == null) false else (roleRepo.findAllApprovedRolesByUserId(context.currentUserId()!!).find { role -> UserRole.Role.ADMIN == role.role } != null)
        if (isAdmin) {
            // If admin, send SMS to everyone in the selected topic
            sendSms(
                    topic = sendTopic!!,
                    message = message
            )?.let { errors ->
                result =  if (errors.isEmpty) Result() else Result(errors = errors.toStringMap(), status = HttpStatus.BAD_REQUEST)
            }
        } else {
            result = Result(status = HttpStatus.FORBIDDEN)
        }

        return result
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