package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.videoFile.IVideoFileRepository

/**
 * Base concrete implementation of the [VideoFileFactory] interface
 *
 * @param videoFileRepo the [IVideoFileRepository] interface
 */
class BaseVideoFileFactory(
        private val videoFileRepo: IVideoFileRepository
) : VideoFileFactory {

    /**
     * Override of the [VideoFileFactory.create] method that will return a [Create] command
     *
     * @param request the [Create.Request] object
     * @return a [Create] command object
     */
    override fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Create(
                request = request,
                videoFileRepo = videoFileRepo
        )
    }
}