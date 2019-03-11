package com.radiotelescope.repository.model.celestialBody

import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.CelestialBodySpecification
import org.springframework.data.jpa.domain.Specification

/**
 * Builder used to build a [CelestialBodySpecification] matching the list of
 * [SearchCriteria] entered by the user
 */
class CelestialBodySpecificationBuilder {
    var params: ArrayList<SearchCriteria> = arrayListOf()

    /**
     * Builder method that will append a new [SearchCriteria] to the [CelestialBodySpecificationBuilder.params]
     * list.
     *
     * @param searchCriteria the [SearchCriteria] object
     */
    fun with(searchCriteria: SearchCriteria): CelestialBodySpecificationBuilder {
        params.add(searchCriteria)

        return this
    }

    /**
     * Will build the [Specification] based on the [params] list
     *
     * @return a [Specification] object or null if the list is empty
     */
    fun build(): Specification<CelestialBody>? {
        if (params.isEmpty())
            return null

        var specification: Specification<CelestialBody> = CelestialBodySpecification(params[0])

        for (i in 1 until params.size) {
            val searchCriteria = params[i]
            specification = Specification.where(specification).or(CelestialBodySpecification(searchCriteria))
        }

        return specification
    }
}