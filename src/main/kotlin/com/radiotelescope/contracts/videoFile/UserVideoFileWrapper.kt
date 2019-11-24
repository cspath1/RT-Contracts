package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.videoFile.VideoFile
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [VideoFileFactory] and is responsible for all
 * user role validations for the Feedback Entity
 *
 * @param factory the [VideoFileFactory] interface
 */
class UserVideoFileWrapper(
        private val context: UserContext,
        private val factory: VideoFileFactory
) {
    /**
     * Wrapper method for the [VideoFileFactory.create] method.
     *
     * @param request the [Create.Request] object
     */
    fun create(request: Create.Request, id: String, profile: String): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request, id, profile)
    }

    /**
     * Wrapper method for the [VideoFileFactory.listBetweenCreationDates] method.
     *
     * @param request the [ListBetweenCreationDates.Request] object
     */
    fun listBetweenCreationDates(request: ListBetweenCreationDates.Request, withAccess: (result: SimpleResult<List<VideoFile>, Multimap<ErrorTag, String>>) -> Unit): AccessReport? {
        return context.require(
                requiredRoles = listOf(UserRole.Role.ADMIN),
                successCommand = factory.listBetweenCreationDates(request)
        ).execute(withAccess)
    }
}