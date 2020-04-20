package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.service.s3.IAwsS3DeleteService

/**
 * Override of the [Command] interface method used to approve or deny
 * a [User.profilePicture] record
 *
 * @param request the [ApproveDeny.Request]
 * @param userRepo the [IUserRepository] interface
 */
class ApproveDeny (
        private val request: Request,
        private val userRepo: IUserRepository,
        private val deleteService: IAwsS3DeleteService
) : Command<User, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. The method allows admins to approve
     * or deny user-submitted [User.profilePicture] records. If approved, the [User.profilePictureApproved]
     * is updated to true. If denied, record is deleted.
     *
     * Any error will result in the method returning an error in the [SimpleResult].
     */
    override fun execute(): SimpleResult<User, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val theUser = userRepo.findById(request.userId).get()

        // If the request is for approval, mark as approved
        if (request.approved) {
            theUser.profilePictureApproved = true
            userRepo.save(theUser)
        } else {
            // Remove from database
            theUser.profilePicture?.let { deleteService.execute(it) }
        }

        return SimpleResult(theUser, null)
    }

    /**
     * Method responsible for constraint checking and validations for the
     * [Request] object.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>{
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if(!userRepo.existsById(this.userId))
                errors.put(ErrorTag.ID, "User does not exist")
        }

        return errors
    }

    /**
     * Data class containing the fields necessary to submit a frontpage picture
     *
     * @param userId the id of the relevant [User]
     * @param approved admin approval or denial
     */
    data class Request (
            val userId: Long,
            val approved: Boolean
    )
}