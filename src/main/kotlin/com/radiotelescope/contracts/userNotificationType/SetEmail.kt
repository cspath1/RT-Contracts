package com.radiotelescope.contracts.userNotificationType

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.userNotificationType.ErrorTag
import com.radiotelescope.repository.userNotificationType.IUserNotificationTypeRepository
import com.radiotelescope.repository.userNotificationType.UserNotificationType


class SetEmail(
        private val userNotificationTypeRepo: IUserNotificationTypeRepository,
        private val request: SetEmail.Request
) :Command<Long, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()
        if (!errors.isEmpty){
            return SimpleResult(null, errors)
        }

        val userNotificationType = userNotificationTypeRepo.findById(request.userId).get()
        val updatedUserNotificationType = userNotificationTypeRepo.save(request.updateEntity(userNotificationType))

        return SimpleResult(updatedUserNotificationType.userId, null)
    }

    private fun validateRequest():  Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!userNotificationTypeRepo.existsById(request.userId)){
            errors.put(ErrorTag.ID, "User #${request.userId} does not exist")
        }
        return errors
    }


    data class Request(
            val userId: Long
    ) :BaseUpdateRequest<UserNotificationType>{
        override fun updateEntity(entity: UserNotificationType): UserNotificationType {
            entity.type = UserNotificationType.NotificationType.EMAIL
            return entity
        }
    }
}