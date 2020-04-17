package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

class UploadProfilePicture(
        private val request: Request,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val userToUpdate = userRepo.findById(request.id).get()
        val updatedUser = userRepo.save(request.updateEntity(userToUpdate))

        return SimpleResult(updatedUser.id, null)
    }

    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (this.profilePictureUrl.length > 256)
                errors.put(com.radiotelescope.contracts.user.ErrorTag.UPLOAD_PROFILE_PICTURE, "Picture URL is too long")
        }

        return errors
    }

    data class Request (
            val id: Long,
            val profilePictureUrl: String,
            val approved: Boolean
    ) : BaseUpdateRequest<User> {
        override fun updateEntity(entity: User): User {
            entity.profilePicture = profilePictureUrl
            return entity
        }
    }
}