package com.radiotelescope.repository.userNotificationType

import org.springframework.stereotype.Repository
import org.springframework.data.repository.CrudRepository

@Repository
interface IUserNotificationTypeRepository: CrudRepository<UserNotificationType, Long>