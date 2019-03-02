package com.radiotelescope.contracts.allottedTimeCap

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.allottedTimeCap.AllottedTimeCap
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Override of the [Command] interface method used for updating
 * a user's allotted time cap
 *
 * @param request the [Request] object
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
class Update(
        private val request: Request,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository
): Command<AllottedTimeCap, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will update the [AllottedTimeCap] object and
     * return it in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors
     */
    override fun execute(): SimpleResult<AllottedTimeCap, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val allottedTimeCap = allottedTimeCapRepo.findByUserId(request.userId)
        allottedTimeCap.allottedTime = request.allottedTime

        return SimpleResult(allottedTimeCapRepo.save(allottedTimeCap), null)
    }

    /**
     * Method responsible for constraint checking and validations that
     * ensures the user Id refers to an existing User entity, the
     * allottedTime is valid, and the user has an approved role
     */
    private fun validateRequest(): Multimap<ErrorTag, String>{
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if(!userRepo.existsById(userId))
                errors.put(ErrorTag.USER_ID, "User #$userId not found")
            if(userRoleRepo.findMembershipRoleByUserId(userId) == null)
                errors.put(ErrorTag.CATEGORY_OF_SERVICE, "User's Category of Service has not yet been approved")
            if(allottedTime != null && allottedTime < 0)
                errors.put(ErrorTag.ALLOTTED_TIME, "Allotted Time must be null or 0 or higher")
        }

        return errors
    }

    /**
     * Data class containing the fields necessary to update a user's allotted time cap.
     * @param userId the User's id
     * @param allottedTime the allotted time cap
     */
    data class Request(
            val userId: Long,
            val allottedTime: Long?
    )
}