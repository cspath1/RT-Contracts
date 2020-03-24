package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.web.multipart.MultipartFile
import com.radiotelescope.service.s3.S3UploadService
import com.radiotelescope.service.s3.Util

class UploadProfilePicture(
        private val request: Request,
        private val userRepo: IUserRepository,
        private val s3UploadService: S3UploadService
) : Command<Long, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val theUser = userRepo.findById(request.id).get()
        val fileName = Util.generateUniqueFilename(request.profilePictureFile.originalFilename!!)
        val uploadPath = Util.generateProfilePictureUploadPath(theUser)
        request.profilePictureUrl = uploadPath + fileName
        val uploadResult = s3UploadService.execute(request.profilePictureFile, request.profilePictureUrl)

        uploadResult.success?.let { success ->
            val updatedUser = userRepo.save(request.updateEntity(theUser))
            return SimpleResult(updatedUser.id, null)
        }

        val uploadErrors = HashMultimap.create<ErrorTag, String>()
        uploadErrors.put(ErrorTag.UPLOAD, "File upload failed")
        return SimpleResult(null, uploadErrors)
    }

    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (userRepo.existsById(id)) {
                if (profilePictureFile.originalFilename == null)
                    errors.put(ErrorTag.UPLOAD, "Invalid file")
            } else {
                errors.put(ErrorTag.ID, "No User was found with specified Id")
                return errors
            }
        }

        return errors
    }

    data class Request(
            val id: Long,
            val profilePictureFile: MultipartFile,
            var profilePictureUrl: String
    ) : BaseUpdateRequest<User> {
        /**
         * Override of the [BaseUpdateRequest.updateEntity] method that will take
         * a user entity and update its values to the values in the request
         */
        override fun updateEntity(entity: User): User {
            // Find the existing user from the repository and update it's information
            entity.profilePicture = profilePictureUrl

            return entity
        }

    }
}