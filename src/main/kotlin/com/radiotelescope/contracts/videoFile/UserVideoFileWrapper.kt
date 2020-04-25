package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.videoFile.VideoFile
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Wrapper that takes a [VideoFileFactory] and is responsible for all
 * user role validations for the VideoFile Entity
 *
 * @property context the [UserContext] interface
 * @property factory the [VideoFileFactory] interface
 */
class UserVideoFileWrapper(
        private val context: UserContext,
        private val factory: VideoFileFactory
) {
    /**
     * Wrapper method for the [VideoFileFactory.create] method.
     *
     * @param request the [Create.Request] object
     * @param uuid the private uuid for submitting videos locally
     * @param profile the current user profile
     * @return a [Command] object
     */
    fun create(request: Create.Request, uuid: String, profile: String): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request, uuid, profile)
    }

    /**
     * Wrapper method for the [VideoFileFactory.retrieveList] method that adds Spring
     * Security authentication to the [RetrieveList] command object.
     *
     * @param pageable contains the pageSize and pageNumber
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun retrieveList(pageable: Pageable, withAccess: (result: SimpleResult<Page<VideoFile>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if(context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.retrieveList(
                            pageable = pageable
                    )
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }

    /**
     * Wrapper method for the [VideoFileFactory.listBetweenCreationDates] method.
     *
     * @param request the [ListBetweenCreationDates.Request] object
     * @param withAccess anonymous function that uses the command's result object
     * @return An [AccessReport] if authentication fails, null otherwise
     */
    fun listBetweenCreationDates(request: ListBetweenCreationDates.Request, withAccess: (result: SimpleResult<List<VideoFile>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        if(context.currentUserId() != null) {
            return context.require(
                    requiredRoles = listOf(UserRole.Role.ADMIN),
                    successCommand = factory.listBetweenCreationDates(request)
            ).execute(withAccess)
        }

        return AccessReport(missingRoles = listOf(UserRole.Role.ADMIN), invalidResourceId = null)
    }
}