package com.radiotelescope.contracts.log

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.model.log.Filter
import com.radiotelescope.repository.model.log.LogSpecificationBuilder
import com.radiotelescope.repository.model.log.SearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.collections.List

/**
 * Override of the [Command] interface method used for Log searching
 *
 * @param searchCriteria a [List] of [SearchCriteria]
 * @param pageable the [Pageable] interface
 * @param logRepo the [ILogRepository] interface
 */
class Search(
        private val searchCriteria: List<SearchCriteria>,
        private val pageable: Pageable,
        private val logRepo: ILogRepository
) : Command<Page<LogInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override fo the [Command.execute] method. Calls the [validateSearch] method that will
     * handle all constraint checking and validation
     *
     * If validation passes, it will execute a custom search using the [LogSpecificationBuilder]
     * which will build a custom search specification based upon the user's criteria. It will then
     * adapt the results into a [Page] of [LogInfo]
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Page<LogInfo>, Multimap<ErrorTag, String>> {
        validateSearch(searchCriteria)?.let { return SimpleResult(null, it) } ?: let {
            // Instantiate the specification builder
            val specificationBuilder = LogSpecificationBuilder()

            // Add each criteria to the builder
            searchCriteria.forEach{ criteria ->
                specificationBuilder.with(criteria)
            }

            // Create the specification using the builder
            val specification = specificationBuilder.build()

            // Make the call
            val logPage = logRepo.findAll(specification, pageable)
            val infoList = arrayListOf<LogInfo>()

            logPage.forEach { log ->
                infoList.add(LogInfo(log))
            }

            val infoPage = PageImpl(infoList, logPage.pageable, logPage.totalElements)


            return SimpleResult(infoPage, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the [List] of [SearchCriteria].
     * Currently, it checks to ensure the list is not empty and each filter has the correct type of value
     *
     * @param searchCriteria the [List] of [SearchCriteria]
     * @return a [Multimap] of errors or null
     */
    private fun validateSearch(searchCriteria: List<SearchCriteria>): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (searchCriteria.isEmpty())
            errors.put(ErrorTag.SEARCH, "No search parameters specified")
        else if (searchCriteria.isNotEmpty()) {
            searchCriteria.forEach { theSearchCriteria ->
                if (theSearchCriteria.filter == Filter.ACTION &&
                        theSearchCriteria.value !is String) {
                    errors.put(ErrorTag.SEARCH, "The specified action value is invalid")
                }

                if (theSearchCriteria.filter == Filter.AFFECTED_TABLE &&
                        !Log.AffectedTable.values().contains(theSearchCriteria.value)) {
                    errors.put(ErrorTag.SEARCH, "The specified table does not exist")
                }

                if (theSearchCriteria.filter == Filter.IS_SUCCESS &&
                        theSearchCriteria.value !is Boolean) {
                    errors.put(ErrorTag.SEARCH, "The specified value must be a boolean")
                }

                if (theSearchCriteria.filter == Filter.STATUS &&
                        theSearchCriteria.value !is Int) {
                    errors.put(ErrorTag.SEARCH, "The specified status must be an integer")
                }
            }
        }

        return if (errors.isEmpty) null else errors
    }
}