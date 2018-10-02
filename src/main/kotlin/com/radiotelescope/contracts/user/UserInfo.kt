package com.radiotelescope.contracts.user

import com.radiotelescope.repository.user.User

data class UserInfo(
        val firstName: String,
        val lastName: String,
        val email: String,
        val id: Long,
        val company: String?,
        val phoneNumber: String?,
        val active: Boolean,
        val status: User.Status
) {
     constructor(user: User) : this(
             firstName = user.firstName,
             lastName =  user.lastName,
             email = user.email,
             id = user.id,
             company = user.company,
             phoneNumber = user.phoneNumber,
             active = user.active,
             status = user.status
     )

}