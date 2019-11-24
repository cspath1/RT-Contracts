package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.videoFile.VideoFile

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
    override fun create(request: Create.Request, id: String, profile: String): Command<Long, Multimap<ErrorTag, String>> {
        return Create(
                request = request,
                videoFileRepo = videoFileRepo,
                id = id,
                profile = profile
        )
    }

    /**
     * Override of the [VideoFileFactory.listBetweenCreationDates] method that will return a [ListBetweenCreationDates] command
     *
     * @param request the [ListBetweenCreationDates.Request] object
     * @return a [ListBetweenCreationDates] command object
     */
    override fun listBetweenCreationDates(request: ListBetweenCreationDates.Request): Command<List<VideoFile>, Multimap<ErrorTag, String>> {
        return ListBetweenCreationDates(
                request = request,
                videoRepo = videoFileRepo
        )
    }
}