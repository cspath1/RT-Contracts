package com.radiotelescope.repository.celestialBody

import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class CelestialBodySpecification(
        private val searchCriteria: SearchCriteria
) : Specification<CelestialBody> {
    override fun toPredicate(root: Root<CelestialBody>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): Predicate? {
        return criteriaBuilder.and(
                criteriaBuilder.like(criteriaBuilder.lower(root.get(searchCriteria.filter.field)), "%" + searchCriteria.value.toString().toLowerCase() + "%"),
                criteriaBuilder.notEqual(root.get<String>("status"), CelestialBody.Status.HIDDEN)
        )
    }
}