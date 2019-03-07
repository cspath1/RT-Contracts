package com.radiotelescope.controller.celestialBody

import com.radiotelescope.contracts.celestialBody.UserCelestialBodyWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.model.celestialBody.Filter
import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * Rest Controller used for dynamically searching for Celestial Body records
 *
 * @param celestialBodyWrapper the [UserCelestialBodyWrapper]
 * @param logger the [Logger] service
 */
@RestController
class CelestialBodySearchController(
        private val celestialBodyWrapper: UserCelestialBodyWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the request parameters
     * and adapting them into the parameters required for the [UserCelestialBodyWrapper.search]
     *
     * Once the parameters have been adapted, it will call the method, and respond according
     * to if the request was authenticated, or whether the request was a success or an error.
     *
     * @param pageNumber the page number
     * @param pageSize the page size
     * @param value the search value
     * @param search the search criteria string
     * @return a [Result] object
     */
    @GetMapping(value = ["/api/celestial-bodies/search"])
    fun execute(@RequestParam(value = "page") pageNumber: Int,
                @RequestParam(value = "size") pageSize: Int,
                @RequestParam(value = "value") value: Any,
                @RequestParam(value = "search") search: String): Result {
        val searchCriteria = getSearchCriteriaFromParam(value, search)
        val pageable = PageRequest.of(pageNumber, pageSize)

        celestialBodyWrapper.search(searchCriteria, pageable) {
            // If the command was a success
            it.success?.let { page ->
                // Create success logs
                page.forEach { info ->
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                    action = "Celestial Body Search",
                                    affectedRecordId = info.id,
                                    status = HttpStatus.OK.value()
                            )
                    )
                }

                result = Result(data = page)
            }
            // Otherwise, it was a failure
            it.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                action = "Celestial Body Search",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }?.let {
            // If we get here, this means the user did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                            action = "Celestial Body Search",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    private fun getSearchCriteriaFromParam(value: Any, search: String): List<SearchCriteria> {
        val tokenizer = StringTokenizer(search, "+")
        val filters = arrayListOf<Filter>()

        while (tokenizer.hasMoreTokens()) {
            val filter = Filter.fromField(tokenizer.nextToken())

            if (filter != null)
                filters.add(filter)
        }

        val searchCriteria = arrayListOf<SearchCriteria>()

        filters.forEach {
            searchCriteria.add(SearchCriteria(it, value))
        }

        return searchCriteria
    }
}