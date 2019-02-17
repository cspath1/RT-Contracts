package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.model.user.Filter
import com.radiotelescope.repository.model.user.SearchCriteria
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class UserSearchController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger) {
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/users/search"])
    fun execute(@RequestParam(value = "page") pageNumber: Int,
                @RequestParam(value = "size") pageSize: Int,
                @RequestParam(value = "value") value: Any,
                @RequestParam(value = "search") search: String): Result {
        val searchCriteria = getSearchCriteriaFromParams(value, search)
        val pageable = PageRequest.of(pageNumber, pageSize)

        userWrapper.search(searchCriteria, pageable) {
            // If the command was a success
            it.success?.let { page ->
                // Create success logs
                page.forEach { info ->
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    Log.AffectedTable.USER,
                                    action = "User Search",
                                    affectedRecordId = info.id
                            )
                    )
                }

                result = Result(data = page)
            }
            // Otherwise, it was a failure
            it.error?.let { error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Search",
                                affectedRecordId = null
                        ),
                        errors = error.toStringMap()
                )

                result = Result(
                        errors = error.toStringMap()
                )
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Search",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    private fun getSearchCriteriaFromParams(value: Any, search: String): List<SearchCriteria> {
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