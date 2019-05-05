package com.radiotelescope.repository.log

import com.radiotelescope.repository.model.log.SearchCriteria
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * Concrete implementation of the [Specification] interface for the [Log] entity
 *
 * @param searchCriteria the [SearchCriteria] object
 */
class LogSpecification(
        private val searchCriteria: SearchCriteria
) : Specification<Log> {
    /**
     * Return a predicate that looks for values [SearchCriteria.filter] field that contain the
     * [SearchCriteria.value]. Case insensitive search.
     */
    override fun toPredicate(root: Root<Log>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): Predicate? {
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(searchCriteria.filter.field)), "%" + searchCriteria.value.toString().toLowerCase() + "%")
    }
}