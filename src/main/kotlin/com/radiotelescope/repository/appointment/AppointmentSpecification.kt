package com.radiotelescope.repository.appointment

import com.radiotelescope.repository.model.appointment.Filter
import com.radiotelescope.repository.model.appointment.SearchCriteria
import com.radiotelescope.repository.user.User
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.*

class AppointmentSpecification(
        private val searchCriteria: SearchCriteria
) : Specification<Appointment> {
    override fun toPredicate(root: Root<Appointment>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): Predicate? {
        if (Filter.userSearchParams().contains(searchCriteria.filter)) {
            val users = root.join<Appointment, User>("user")
            // Filters that are not compatible with other filters
            if (!searchCriteria.filter.multiCompatible) {
                when (searchCriteria.filter) {
                    Filter.USER_FULL_NAME -> {
                        val searchValue = searchCriteria.value as String
                        val firstName = searchValue.substring(0, searchValue.indexOf(" "))
                        val lastName = searchValue.substring(searchValue.indexOf(" ") + 1)
                        criteriaBuilder.and(
                                criteriaBuilder.like(criteriaBuilder.lower(users.get("firstName")), "%" +  firstName.toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(users.get("lastName")), "%" + lastName.toLowerCase() + "%")
                        )
                    }
                    else -> {
                        // Unknown filter found
                        return null
                    }
                }
            }
            // Filters that are compatible with other filters can
            // be grouped together
            else {
                criteriaBuilder.and(
                        criteriaBuilder.like(criteriaBuilder.lower(users.get(searchCriteria.filter.field)), "%" + searchCriteria.value.toString().toLowerCase() + "%")
                )
            }

            return criteriaBuilder.not(root.get<String>("status").`in`(listOf(Appointment.Status.CANCELED, Appointment.Status.REQUESTED)))
        } else {
            // Current no other search parameters supported
            // This will change in the future
            return null
        }
    }
}