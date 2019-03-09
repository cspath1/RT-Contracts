package com.radiotelescope.repository.model.user

import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.user.UserSpecification
import org.springframework.data.jpa.domain.Specification

/**
 * Builder used to build a [UserSpecification] matching the list of
 * [SearchCriteria] entered by the user.
 */
class UserSpecificationBuilder {
    var params: ArrayList<SearchCriteria> = arrayListOf()

    /**
     * Builder method that will append a new [SearchCriteria] to the [UserSpecificationBuilder.params] list
     *
     * @param searchCriteria the [SearchCriteria] object
     */
    fun with(searchCriteria: SearchCriteria): UserSpecificationBuilder {
        params.add(searchCriteria)

        return this
    }

    /**
     * Will build the [Specification] based on the [params] list
     *
     * @return a [Specification] object or null if the list is empty
     */
    fun build(): Specification<User>? {
        if (params.isEmpty())
            return null

        var specification: Specification<User> = UserSpecification(params[0])

        for (i in 1 until params.size) {
            val searchCriteria = params[i]
            specification = Specification.where(specification).or(UserSpecification(searchCriteria))
        }

        return specification
    }
}