package com.radiotelescope.controller.appointment

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.model.appointment.Filter
import com.radiotelescope.repository.model.appointment.SearchCriteria
import com.radiotelescope.toStringMap
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * REST Controller to handle dynamically searching for appointments
 *
 * @param appointmentWrapper the [UserAppointmentWrapper]
 * @param logger the [Logger] service
 */
@RestController
class AppointmentSearchController(
        private val appointmentWrapper: UserAppointmentWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the request parameters
     * and adapting them into the parameters required for the [UserAppointmentWrapper.search]
     *
     * Once the parameters have been adapted, it will call the method, and respond according
     * to if the request was successfully authenticated, or whether the request was
     * a success or an error
     *
     * @param pageNumber the page number
     * @param pageSize the page size
     * @param value the search value
     * @param search the search criteria string
     * @return a [Result] object
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @GetMapping(value = ["/api/appointments/search"])
    fun execute(@RequestParam(value = "page") pageNumber: Int,
                @RequestParam(value = "size") pageSize: Int,
                @RequestParam(value = "value") value: Any,
                @RequestParam(value = "search") search: String): Result {
        val searchCriteria = getSearchCriteriaFromParam(value, search)
        val pageable = PageRequest.of(pageNumber, pageSize)

        appointmentWrapper.search(searchCriteria, pageable) {
            // If the command was a success
            it.success?.let { page ->
                // Create success logs
                page.forEach { info ->
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.APPOINTMENT,
                                    action = "Appointment Search",
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
                                affectedTable = Log.AffectedTable.APPOINTMENT,
                                action = "Appointment Search",
                                affectedRecordId = null
                        ),
                        errors = error.toStringMap()
                )

                result = Result(errors = error.toStringMap())
            }
        }?.let {
            // If we get here, this means the User did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Appointment Search",
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }

    /**
     * Private method that will take the search string and value from the request
     * and adapt them into a [List] of [SearchCriteria].
     *
     * @param value the search value
     * @param search the search criteria string
     * @return a [List] of [SearchCriteria]
     */
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