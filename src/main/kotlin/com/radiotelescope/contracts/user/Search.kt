package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.model.user.SearchCriteria
import com.radiotelescope.repository.model.user.UserSpecificationBuilder
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.collections.List

class Search(
        private val searchCriteria: List<SearchCriteria>,
        private val pageable: Pageable,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
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

            // Adapt the user page into a page of info objects
            userPage.forEach { user ->
                val theUserRole = userRoleRepo.findMembershipRoleByUserId(user.id)

                val theRole = theUserRole?.role
                infoList.add(UserInfo(user, theRole?.label))
            }

            val infoPage = PageImpl(infoList, userPage.pageable, userPage.totalElements)

            // Return the page of info objects
            return SimpleResult(infoPage, null)
        }
    }

    private fun validateSearch(searchCriteria: List<SearchCriteria>): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (searchCriteria.isEmpty())
            errors.put(ErrorTag.SEARCH, "No search parameters specified")

        return if (errors.isEmpty) null else errors
    }
}