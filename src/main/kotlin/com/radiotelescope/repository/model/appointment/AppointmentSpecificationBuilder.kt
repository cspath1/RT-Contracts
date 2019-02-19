package com.radiotelescope.repository.model.appointment

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.AppointmentSpecification
import org.springframework.data.jpa.domain.Specification

class AppointmentSpecificationBuilder {
    var params: ArrayList<SearchCriteria> = arrayListOf()

    fun with(searchCriteria: SearchCriteria): AppointmentSpecificationBuilder {
        params.add(searchCriteria)

        return this
    }

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