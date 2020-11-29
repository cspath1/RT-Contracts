package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface method used for updating a User's profile picture
 *
 * @param request the [Request] object
 * @param userRepo the [IUserRepository]
 */
class UpdateProfilePicture(
        private val request: UpdateProfilePicture.Request,
        private val userRepo: IUserRepository
): Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It will update and persist the [User] object.
     * It will then return a [SimpleResult] object with the [User] id and a null errors field.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!userRepo.existsById(request.id)) {
            errors.put(ErrorTag.ID, "No User was found with specified Id")
            return SimpleResult(null, errors)
        }

        val user = userRepo.findById(request.id).get()
        val updatedUser = userRepo.save(request.updateEntity(user))

        return SimpleResult(updatedUser.id, null)
    }

    /**
     * Data class containing all fields necessary for user profile picture update. Implements the
     * [BaseUpdateRequest] interface and overrides the [BaseUpdateRequest.updateEntity] method
     */
    data class Request(
            val id: Long,
            val profilePicture: String,
            val profilePictureApproved: Boolean
    ) : BaseUpdateRequest<User> {
        override fun updateEntity(entity: User): User {
            entity.id = id
            entity.profilePicture = profilePicture
            entity.profilePictureApproved = profilePictureApproved

            return entity
        }
    }
}