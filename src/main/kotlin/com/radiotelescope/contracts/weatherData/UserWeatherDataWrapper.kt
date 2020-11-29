package com.radiotelescope.contracts.weatherData

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.weatherData.WeatherData
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper that takes a [WeatherDataFactory] and is responsible for all
 * user role validations for the Feedback Entity
 *
 * @property context the [UserContext] interface
 * @param factory the [WeatherDataFactory] interface
 */

class UserWeatherDataWrapper (
        private val context: UserContext,
        private val factory: WeatherDataFactory
){
    /**
     * Wrapper method for the [WeatherDataFactory.create] method.
     *
     * @param request the [Create.Request] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request)
    }


    /**
     * Wrapper method for the [.retrieveList] method that adds Spring
     * Security authentication to the [RetrieveList] command object.
     *
     * @param pageable contains the pageSize and pageNumber
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieveList(pageable: Pageable, withAccess: (result: SimpleResult<Page<WeatherData>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if(context.currentUserId() != null) {
            return context.requireAny(
                    requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.ALUMNUS),
                    successCommand = factory.retrieveList(
                            pageable = pageable
                    )
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [WeatherDataFactory.listBetweenCreationDates] method.
     *
     * @param request the [ListBetweenCreationDates.Request] object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun listBetweenCreationDates(request: ListBetweenCreationDates.Request, withAccess: (result: SimpleResult<List<WeatherData>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if(context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.listBetweenCreationDates(request)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }
}