package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.videoFile.VideoFile

/**
 * Wrapper that takes a [VideoFileFactory] and is responsible for all
 * user role validations for the Feedback Entity
 *
 * @param factory the [VideoFileFactory] interface
 */
class UserVideoFileWrapper(
        private val factory: VideoFileFactory
) {
    /**
     * Wrapper method for the [VideoFileFactory.create] method.
     *
     * @param request the [Create.Request] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return factory.create(request)
    }

    /**
     * Wrapper method for the [VideoFileFactory.listBetweenCreationDates] method.
     *
     * @param request the [ListBetweenCreationDates.Request] object
     */
    fun listBetweenCreationDates(request: ListBetweenCreationDates.Request): Command<List<VideoFile>, Multimap<ErrorTag, String>> {
        return factory.listBetweenCreationDates(request)
    }
}