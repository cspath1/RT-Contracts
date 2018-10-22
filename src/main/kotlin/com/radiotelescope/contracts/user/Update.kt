package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface method used for User registration
 *
 * @param request the [Request] object
 * @param userRepo the [IUserRepository]
 */
class Update(
        private val request: Update.Request,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>>{
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes, it will update and persist the [User] object and update
     * the [UserRole] associated with it. It will then return a [SimpleResult]
     * object with the [User] id and a null errors field.
     *
     * If validation fields, it will return a [SimpleResult] with the errors and a
     * null success field
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val user = userRepo.findById(request.id).get()
        val updatedUser = userRepo.save(request.updateEntity(user))
        return SimpleResult(updatedUser.id, null)
    }

    /**
     * Method responsible for constraint checking and validations for the user
     * create request. It will ensure the first name and last name are not blank and
     * are under the max length, that the email is not blank, is a valid email, and
     * is not already in use and that the password is not blank and matches the
     * password confirm field
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (userRepo.existsById(id)) {
                if (firstName.isBlank())
                    errors.put(ErrorTag.FIRST_NAME, "First Name may not be blank")
                if (firstName.length > 100)
                    errors.put(ErrorTag.FIRST_NAME, "First Name must be under 100 characters")
                if (lastName.isBlank())
                    errors.put(ErrorTag.LAST_NAME, "Last Name may not be blank")
                if (lastName.length > 100)
                    errors.put(ErrorTag.LAST_NAME, "Last Name must be under 100 characters")
                if (email.isBlank())
                    errors.put(ErrorTag.EMAIL, "Email Address may not be blank")
                if (!User.isEmailValid(email))
                    errors.put(ErrorTag.EMAIL, "Invalid Email Address")
                if (userRepo.existsByEmail(email) && userRepo.findById(id).get().email != email)
                    errors.put(ErrorTag.EMAIL, "Email Address is already in use")
            } else {
                errors.put(ErrorTag.ID, "No User was found with specified Id")
                return errors
            }
        }

        return errors
    }


    /**
     * Data class containing all fields necessary for user update. Implements the
     * [BaseUpdateRequest] interface and overrides the [BaseUpdateRequest.updateEntity]
     * method
     */
    data class Request(
            val id: Long,
            val firstName: String,
            val lastName: String,
            val email: String,
            val phoneNumber: String?,
            val company: String?
    ) : BaseUpdateRequest<User> {
        override fun updateEntity(user: User): User {
            // Find the existing user from the repository and update it's information
            user.firstName = firstName
            user.lastName = lastName
            user.email = email

            if (!phoneNumber.isNullOrBlank())
                user.phoneNumber = phoneNumber
            if (!company.isNullOrBlank())
                user.company = company

            return user
        }
    }
}