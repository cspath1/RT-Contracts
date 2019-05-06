package com.radiotelescope.repository.log

import com.radiotelescope.repository.model.log.Filter
import com.radiotelescope.repository.model.log.SearchCriteria
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.*

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
     * [SearchCriteria.value].
     */
    override fun toPredicate(root: Root<Log>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): Predicate? {
        if(searchCriteria.filter.label === Filter.ACTION.label) {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(searchCriteria.filter.field)), "%" + searchCriteria.value.toString().toLowerCase() + "%")
        } else if(searchCriteria.filter.label != Filter.ACTION.label){
            return criteriaBuilder.isTrue(root.get<String>(searchCriteria.filter.field).`in`(searchCriteria.value))
        }else {
            return null
        }
    }
}