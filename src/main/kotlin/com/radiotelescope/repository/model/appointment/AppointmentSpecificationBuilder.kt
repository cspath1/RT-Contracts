package com.radiotelescope.repository.model.appointment

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.AppointmentSpecification
import org.springframework.data.jpa.domain.Specification

/**
 * Builder used to build an [AppointmentSpecification] matching the list of
 * [SearchCriteria] entered by the user.
 */
class AppointmentSpecificationBuilder {
    var params: ArrayList<SearchCriteria> = arrayListOf()

    /**
     * Builder method that will append a new [SearchCriteria] to the [AppointmentSpecificationBuilder.params] list
     *
     * @param searchCriteria the [SearchCriteria] object
     */
    fun with(searchCriteria: SearchCriteria): AppointmentSpecificationBuilder {
        params.add(searchCriteria)

        return this
    }

    /**
     * Will build the [Specification] based on the [params] list
     *
     * @return a [Specification] object
     */
    fun build(): Specification<Appointment>? {
        if (params.isEmpty())
            return null

        var specification: Specification<Appointment> = AppointmentSpecification(params[0])

        for (i in 1 until params.size) {
            val searchCriteria = params[i]
            specification = Specification.where(specification).or(AppointmentSpecification(searchCriteria))
        }

        return specification
    }
}