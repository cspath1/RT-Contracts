package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User

class ChangePassword(
        private val request: Request,
        private val userRepo: IUserRepository
) : Command<Long, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()
        if(!errors.isEmpty)
            return SimpleResult(null, errors)

        val user = userRepo.findById(request.id).get()
        val updatedUser = userRepo.save(request.updateEntity(user))

        return SimpleResult(updatedUser.id, null)
    }

    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (password.isBlank() || currentPassword.isBlank() || passwordConfirm.isBlank())
                errors.put(ErrorTag.PASSWORD, "Password may not be blank")
            if (password != passwordConfirm)
                errors.put(ErrorTag.PASSWORD_CONFIRM, "Passwords do not match")
            if (password == currentPassword)
                errors.put(ErrorTag.PASSWORD, "New password may not be the same as old password")
            if (!password.matches(User.passwordRegex))
                errors.put(ErrorTag.PASSWORD, User.passwordErrorMessage)
        }

        return errors
    }

    data class Request(
            val currentPassword: String,
            val password: String,
            val passwordConfirm: String,
            val id: Long
    ) : BaseUpdateRequest<User> {
        override fun updateEntity(entity: User): User {
            // Uses SHA-1 by default. Adds the salt value (secret)
            // to the password and encrypts it 50 times, specifying
            // a hash size of 256
            val passwordEncoder = User.rtPasswordEncoder
            entity.password = passwordEncoder.encode(password)

            return entity
        }
    }
}



