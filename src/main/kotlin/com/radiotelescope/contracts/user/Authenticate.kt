package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.loginAttempt.LoginAttempt
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import java.util.*

/**
 * Override of the [Command] interface method used for User authentication
 *
 * @param request the [Request] object
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 * @param loginAttemptRepo the [ILoginAttemptRepository] interface
 */
class Authenticate(
        private val request: Request,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository,
        private val loginAttemptRepo: ILoginAttemptRepository
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

        if (!errors.isEmpty) {
            trackLoginAttempt()
            return SimpleResult(null, errors)
        }

        val theUser = userRepo.findByEmail(request.email)

        // Make sure to delete past failed login attempts
        val theLoginAttempts = loginAttemptRepo.findByUserId(theUser!!.id)
        loginAttemptRepo.deleteAll(theLoginAttempts)

        val theUserRole = userRoleRepo.findMembershipRoleByUserId(theUser.id)
        val theRole = theUserRole?.role
        val allottedTime = allottedTimeCapRepo.findByUserId(theUser.id).allottedTime

        return SimpleResult(UserInfo(theUser, theRole?.label, allottedTime), null)
    }

    /**
     * Method responsible for constraint checking and validations for the user
     * login request. It will ensure the email and password are not blank. After,
     * it will check to see if the email exists in the User Table. Then it will make
     * sure the password entered matches the password in the database.
     *
     * It also checks to see if the user exceeded the number of login attempt
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (email.isBlank())
                errors.put(ErrorTag.EMAIL, "Invalid Email or Password")
            if (password.isBlank())
                errors.put(ErrorTag.PASSWORD, "Invalid Email or Password")
            if (!userRepo.existsByEmail(email)) {
                errors.put(ErrorTag.EMAIL, "Invalid Email or Password")
            }
            if (userRepo.existsByEmail(email)) {
                val loginAttempts = loginAttemptRepo.findByUserId(userRepo.findByEmail(email)!!.id)
                if (!loginAttempts.isEmpty() && loginAttempts.size >= 5)
                    errors.put(ErrorTag.LOGIN_ATTEMPT, "Your Account is Locked")
            }
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
     * Method responsible for keeping track of the number of failed login attempts.
     */
    private fun trackLoginAttempt() {
        with(request) {
            // Failed login attempts can only be tracked for email's associated with a user
            // In other words, invalid email addresses will not be tracked
            if (userRepo.existsByEmail(email)) {
                val attempt = LoginAttempt(Date(System.currentTimeMillis()))
                attempt.user = userRepo.findByEmail(email)!!

                loginAttemptRepo.save(attempt)
            }
        }
    }

    /**
     * Data class containing the fields necessary to log a user in.
     */
    data class Request(
            val email: String,
            val password: String
    )
}