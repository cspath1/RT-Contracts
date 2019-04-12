package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.info.*
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.model.appointment.AppointmentSpecificationBuilder
import com.radiotelescope.repository.model.appointment.Filter
import com.radiotelescope.repository.model.appointment.SearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface method used for Appointment searching
 *
 * @param searchCriteria a [List] of [SearchCriteria]
 * @param pageable the [Pageable] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 */
class Search(
        private val searchCriteria: List<SearchCriteria>,
        private val pageable: Pageable,
        private val appointmentRepo: IAppointmentRepository
) : Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateSearch] method that will
     * handle all constraint checking and validation.
     *
     * If validation passes, it will execute a customer search using the [AppointmentSpecificationBuilder]
     * which will build a custom search specification based on the user's criteria. It will then
     * adapt the results into a [Page] of [AppointmentInfo].
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Page<AppointmentInfo>, Multimap<ErrorTag, String>> {
        validateSearch(searchCriteria)?.let { return SimpleResult(null, it) } ?: let {
            // Instantiate the specification builder
            val specificationBuilder = AppointmentSpecificationBuilder()

            // Add each search criteria to the builder
            searchCriteria.forEach { criteria ->
                specificationBuilder.with(criteria)
            }

            // Create the specification using the builder
            val specification = specificationBuilder.build()

            // Make the call
            val appointmentPage = appointmentRepo.findAll(specification, pageable)
            val infoList = arrayListOf<AppointmentInfo>()

            // Type-based info assignment
            appointmentPage.forEach { appointment ->
                when (appointment.type) {
                    Appointment.Type.POINT -> infoList.add(PointAppointmentInfo(appointment))
                    Appointment.Type.DRIFT_SCAN -> infoList.add(DriftScanAppointmentInfo(appointment))
                    Appointment.Type.CELESTIAL_BODY -> infoList.add(CelestialBodyAppointmentInfo(appointment))
                    Appointment.Type.RASTER_SCAN -> MultiPointAppointmentInfo(appointment)
                    Appointment.Type.FREE_CONTROL -> MultiPointAppointmentInfo(appointment)
                }
            }

            val infoPage = PageImpl(infoList, appointmentPage.pageable, appointmentPage.totalElements)

            return SimpleResult(infoPage, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the [List] of [SearchCriteria].
     * Currently, it checks to ensure the list is not empty, and if the list size is two or more,
     * will make sure all filters are compatible with each other
     *
     * @param searchCriteria the [List] of [SearchCriteria]
     * @return a [Multimap] of errors or null
     */
    private fun validateSearch(searchCriteria: List<SearchCriteria>): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (searchCriteria.isEmpty()) {
            errors.put(ErrorTag.SEARCH, "No search parameters specified")
        } else if (searchCriteria.size == 1) {
            // Handle individual constraints here
            val theSearchCriteria = searchCriteria[0]
            // Full names must have a first and last name supplied
            if (theSearchCriteria.filter == Filter.USER_FULL_NAME) {
                if (!theSearchCriteria.value.toString().trim().contains(" ")) {
                    errors.put(ErrorTag.SEARCH, "First and Last Name must be supplied")
                }
            }
        } else {
            // If there is more than one search criteria, check to make sure
            // all of them are compatible with each other
            val incompatibleSearchParams = searchCriteria.any { !it.filter.multiCompatible }

            if (incompatibleSearchParams) {
                val incompatibleParam = searchCriteria.first { !it.filter.multiCompatible }
                errors.put(ErrorTag.SEARCH, "Search filter '${incompatibleParam.filter.label}' is not compatible with other criteria")
            }
        }

        return if (errors.isEmpty) null else errors
    }
}