package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.model.user.SearchCriteria
import com.radiotelescope.repository.model.user.UserSpecificationBuilder
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.collections.List

/**
 * Override of the [Command] interface method used for User searching
 *
 * @param searchCriteria a [List] of [SearchCriteria]
 * @param pageable the [Pageable] interface
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 */
class Search(
        private val searchCriteria: List<SearchCriteria>,
        private val pageable: Pageable,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateSearch] method that will
     * handle all constraint checking and validation.
     *
     * If validation passes, it will execute a custom search using the [UserSpecificationBuilder]
     * which will build a custom search specification based upon the user's criteria. It will then
     * adapt the results into a [Page] of [UserInfo].
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>> {
        validateSearch(searchCriteria)?.let { return SimpleResult(null, it) } ?: let {
            // Instantiate the specification builder
            val specificationBuilder = UserSpecificationBuilder()

            // Add each search criteria to the builder
            searchCriteria.forEach { criteria ->
                specificationBuilder.with(criteria)
            }

            // Create the specification using the builder
            val specification = specificationBuilder.build()

            // Make the call
            val userPage = userRepo.findAll(specification, pageable)
            val infoList = arrayListOf<UserInfo>()

            // Grab the total elements from the page result
            var totalElements = userPage.totalElements

            // Adapt the user page into a page of info objects
            userPage.forEach { user ->
                val theUserRole = userRoleRepo.findMembershipRoleByUserId(user.id)

                val theRole = theUserRole?.role

                // Ignore adding admin users
                if (theRole != UserRole.Role.ADMIN) {
                    infoList.add(UserInfo(user, theRole?.label))

                } else {
                    // When there is an admin user, subtract
                    // the total elements so the custom page
                    // result is correctly instantiated
                    totalElements--
                }
            }

            val infoPage = PageImpl(infoList, userPage.pageable, totalElements)

            // Return the page of info objects
            return SimpleResult(infoPage, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the [List] of [SearchCriteria].
     * Currently, it just checks to ensure the list is not empty
     *
     * @param searchCriteria the [List] of [SearchCriteria]
     * @return a [Multimap] of errors or null
     */
    private fun validateSearch(searchCriteria: List<SearchCriteria>): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (searchCriteria.isEmpty())
            errors.put(ErrorTag.SEARCH, "No search parameters specified")

        return if (errors.isEmpty) null else errors
    }
}