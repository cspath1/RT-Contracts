package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface for a user to "delete" their account.
 *
 * @param id the [User] id
 * @param userRepo the [IUserRepository] interface
 */
class Delete(
        private val id: Long,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It calls the [validateRequest]
     * method to handle validation and will return errors if validation fails.
     *
     * Otherwise, it will "deleted" the users account.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theUser = userRepo.findById(id).get()
            if (theUser.status == User.Status.Deleted) {
              val errors = HashMultimap.create<ErrorTag, String>()
                errors.put(ErrorTag.STATUS, "Status of userId ${theUser.id} is already deleted")
                return SimpleResult(null, errors)
            }
                disableUser(theUser)
            return SimpleResult(theUser.id, null)
        }
    }

    /**
     * Private method that will set the User's status to DELETED and
     * active flag to false
     */
    private fun disableUser(user: User) {
        user.status = User.Status.DELETED
        user.active = false
        userRepo.save(user)
    }

    /**
     * Private method in charge of ensuring the request passes validation
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!userRepo.existsById(id)) {
            errors.put(ErrorTag.ID, "User Id #$id not found")
        }

        return if (errors.isEmpty) null else errors
    }
}