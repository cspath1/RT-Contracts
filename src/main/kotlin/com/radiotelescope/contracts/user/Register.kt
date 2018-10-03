package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder

class Register(
        private val request: Request,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : Command<Long, Multimap<ErrorTag, String>>{
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes, it will create and persist the [User] object and create
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

        val newUser = userRepo.save(request.toEntity())
        generateUserRoles(newUser)
        return SimpleResult(newUser.id, null)
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
            if (userRepo.existsByEmail(email))
                errors.put(ErrorTag.EMAIL, "Email Address is already in use")
            if (password.isBlank())
                errors.put(ErrorTag.PASSWORD, "Password may not be blank")
            if (password != passwordConfirm)
                errors.put(ErrorTag.PASSWORD_CONFIRM, "Passwords do not match")
            if (!password.matches(User.passwordRegex))
                errors.put(ErrorTag.PASSWORD, User.passwordErrorMessage)
        }

        return errors
    }

    /**
     * Private method to generate and save a base [UserRole] of type
     * USER as well as the category of service entered in the [Request]
     * data class
     */
    private fun generateUserRoles(user: User) {
        // Generate the basic user UserRole
        val role = UserRole(
                role = UserRole.Role.USER,
                userId = user.id
        )

        // TODO: Change the accepted field to false when confirming a user's account is implemented
        role.approved = true

        userRoleRepo.save(role)

        // Generate the categoryOfService UserRole
        val categoryRole = UserRole(
                role = request.categoryOfService,
                userId = user.id
        )

        // TODO: Change the accepted field to false once the admin can accept/decline a user's role
        categoryRole.approved = true

        userRoleRepo.save(categoryRole)
    }

    /**
     * Data class containing all fields necessary for user creation. Implements the
     * [BaseCreateRequest] interface and overrides the [BaseCreateRequest.toEntity]
     * method
     */
    data class Request(
            val firstName: String,
            val lastName: String,
            val email: String,
            val phoneNumber: String?,
            val password: String,
            val passwordConfirm: String,
            val company: String?,
            val categoryOfService: UserRole.Role
    ) : BaseCreateRequest<User> {
        override fun toEntity(): User {
            // Uses SHA-1 by default. Adds the salt value (secret)
            // to the password and encrypts it 50 times, specifying
            // a hash size of 256
            val passwordEncoder = Pbkdf2PasswordEncoder(
                   "YCAS2018",
                    50,
                    256
            )
            val encryptedPassword = passwordEncoder.encode(password)
            val user = User(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = encryptedPassword
            )

            if (!phoneNumber.isNullOrBlank())
                user.phoneNumber = phoneNumber
            if (!company.isNullOrBlank())
                user.company = company

            user.active = true
            user.status = User.Status.Active

            return user
        }
    }
}