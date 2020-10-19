package com.radiotelescope.contracts.user

import com.radiotelescope.repository.user.User

/**
 * Data class representing a read-only model of the
 * [User] Entity
 *
 * @param id the User's id
 * @param firstName the User's first name
 * @param lastName the User's last name
 * @param email the User's email address
 * @param company the User's associated company
 * @param phoneNumber the User's phone number
 * @param active the User's active status
 * @param status the User's status
 * @param notificationType the users preferred method of notification
 * @param firebaseID the users firebase ID
 * @param membershipRole the User's membership role (i.e not the base USER role),
 * given that it has been accepted by an admin
 * @param allottedTime the User's allotted time
 */
data class UserInfo(
        val id: Long,
        val firstName: String,
        val lastName: String,
        val email: String,
        val company: String?,
        val phoneNumber: String?,
        val active: Boolean,
        val status: String,
        // val notificationType: String,
        val firebaseID: String?,
        val membershipRole: String?,
        val allottedTime: Long?
) {
    /**
     * Secondary constructor that takes a user object to set
     * all fields

     * @param user the User
     * @param userRoleLabel the UserRole string
     * @param allottedTime The User's allotted time
     */
    constructor(user: User, userRoleLabel: String?, allottedTime: Long?) : this(
            id = user.id,
            firstName = user.firstName,
            lastName =  user.lastName,
            email = user.email,
            company = user.company,
            phoneNumber = user.phoneNumber,
            active = user.active,
            status = user.status.label,
            // notificationType = user.NotificationType.valueOf(notificationType),
            firebaseID = user.firebaseID,
            membershipRole = userRoleLabel,
            allottedTime = allottedTime
    )
}