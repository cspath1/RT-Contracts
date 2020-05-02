package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import kotlin.collections.List

/**
 * Wrapper that takes a [CelestialBodyFactory] and is responsible for all
 * user role validations for the Celestial Body Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [CelestialBodyFactory] interface
 */
class UserCelestialBodyWrapper(
        private val context: UserContext,
        private val factory: CelestialBodyFactory
) {
    /**
     * Wrapper method for the [CelestialBodyFactory.create] method used to add Spring Security
     * authentication to the [Create] command object
     *
     * @param request the [Create.Request]
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun create(request: Create.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.create(request)
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [CelestialBodyFactory.retrieve] method used to add Spring Security
     * authentication to the [Retrieve] command object
     *
     * @param id the Celestial Body id
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieve(id: Long, withAccess: (result: SimpleResult<CelestialBodyInfo, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.requireAny(
                requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.ALUMNUS),
                successCommand = factory.retrieve(id)
        ).execute(withAccess)
    }

    /**
     * Wrapper method for the [CelestialBodyFactory.list] method used to add Spring Security
     * authentication to the [List] command object
     *
     * @param pageable the [Pageable]
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun list(pageable: Pageable, withAccess: (result: SimpleResult<Page<CelestialBodyInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.requireAny(
                requiredRoles = listOf(UserRole.Role.ADMIN, UserRole.Role.ALUMNUS),
                successCommand = factory.list(pageable)
        ).execute(withAccess)
    }

    /**
     * Wrapper method for [CelestialBodyFactory.search] method used to add Spring
     * Security authentication to the [Search] command object
     *
     * @param searchCriteria the [SearchCriteria]
     * @param pageable the [Pageable] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun search(searchCriteria: List<SearchCriteria>, pageable: Pageable, withAccess: (result: SimpleResult<Page<CelestialBodyInfo>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if (context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(),
                    successCommand = factory.search(searchCriteria, pageable)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.USER), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [CelestialBodyFactory.update] method used to add Spring
     * Security authentication to the [Update] command object
     *
     * @param request the [Update.Request] object
     * @param withAccess anonymous function that uses the command's result object
     * @return an [AccessReport] if authentication fails, null otherwise
     */
    fun update(request: Update.Request, withAccess: (result: SimpleResult<Long, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.update(request)
        ).execute(withAccess)
    }
}