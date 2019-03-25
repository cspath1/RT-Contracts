package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

/**
 * Override of the [Command] interface method used for User authentication
 *
 * @param request the [Request] object
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
class Authenticate(
        private val request: Request,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
) : Command<UserInfo, Multimap<ErrorTag, String>>{
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If the validation passes, it will return a [UserInfo] object.
     *
     * If validation fails, it will the errors
     */
    override fun execute(): SimpleResult<UserInfo, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val theUser = userRepo.findByEmail(request.email)
        val theUserRole = userRoleRepo.findMembershipRoleByUserId(theUser!!.id)
        val theRole = theUserRole?.role
        val allottedTime = allottedTimeCapRepo.findByUserId(theUser.id).allottedTime

        return SimpleResult(UserInfo(theUser, theRole?.label, allottedTime), null)
    }

    /**
     * Method responsible for constraint checking and validations for the user
     * login request. It will ensure the email and password are not blank. After,
     * it will check to see if the email exists in the User Table. Then it will make
     * sure the password entered matches the password in the database
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (email.isBlank())
                errors.put(ErrorTag.EMAIL, "Invalid Email or Password")
            if (password.isBlank())
                errors.put(ErrorTag.PASSWORD, "Invalid Email or Password")
            if (!userRepo.existsByEmail(email))
                errors.put(ErrorTag.EMAIL, "Invalid Email or Password")
        }

        if (!errors.isEmpty)
            return errors

        val theUser = userRepo.findByEmail(request.email)
        val passwordEncoder = User.rtPasswordEncoder

        if (!passwordEncoder.matches(request.password, theUser?.password))
            errors.put(ErrorTag.PASSWORD, "Invalid Email or Password")

        return errors
    }

    /**
     * Data class containing the fields necessary to log a user in.
     */
    data class Request(
            val email: String,
            val password: String
    )
}