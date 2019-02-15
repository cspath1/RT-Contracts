package com.radiotelescope.repository.user

import com.radiotelescope.repository.model.user.SearchCriteria
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * Concrete implementation of the [Specification] interface for the [User] entity
 *
 * @param searchCriteria the [SearchCriteria] object
 */
class UserSpecification(
        private val searchCriteria: SearchCriteria
) : Specification<User> {
    /**
     * Returns a predicate that looks for values [SearchCriteria.filter] field that contain the
     * [SearchCriteria.value]
     */
    override fun toPredicate(root: Root<User>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): Predicate? {
        return criteriaBuilder.like(root.get(searchCriteria.filter.field), "%" + searchCriteria.value + "%")
    }
}