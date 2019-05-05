package com.radiotelescope.repository.model.log

import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.log.LogSpecification
import org.springframework.data.jpa.domain.Specification


/**
 * Builder used to build a [LogSpecification] matching the list of
 * [SearchCriteria] entered by the user.
 */
class LogSpecificationBuilder {
    var params: ArrayList<SearchCriteria> = arrayListOf()

    /**
     * Builder method that will append a new [SearchCriteria] to the [LogSpecificationBuilder.params] list
     *
     * @param searchCriteria the [SearchCriteria] object
     */
    fun with(searchCriteria: SearchCriteria): LogSpecificationBuilder {
        params.add(searchCriteria)

        return this
    }

    /**
     * Will build the [Specification] based on the [params] list
     *
     * @return a [Specification] object
     */
    fun build(): Specification<Log>? {
        if (params.isEmpty())
            return null

        var specification: Specification<Log> = LogSpecification(params[0])

        for(i in 1 until params.size) {
            val searchCriteria = params[i]
            specification = Specification.where(specification).or(LogSpecification(searchCriteria))
        }

        return specification
    }
}