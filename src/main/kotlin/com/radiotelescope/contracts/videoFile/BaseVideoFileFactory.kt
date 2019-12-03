package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.videoFile.VideoFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

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
    override fun create(request: Create.Request, uuid: String, profile: String): Command<Long, Multimap<ErrorTag, String>> {
        return Create(
                request = request,
                videoFileRepo = videoFileRepo,
                uuid = uuid,
                profile = profile
        )
    }

    /**
     * Override of the [VideoFileFactory.retrieveList] method that will return a [RetrieveList] command
     *
     * @param pageable the [Pageable] object that has the page number and page size
     * @return a [RetrieveList] command object
     */
    override fun retrieveList(pageable: Pageable): Command<Page<VideoFile>, Multimap<ErrorTag, String>> {
        return RetrieveList(
                pageable = pageable,
                videoFileRepo = videoFileRepo
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