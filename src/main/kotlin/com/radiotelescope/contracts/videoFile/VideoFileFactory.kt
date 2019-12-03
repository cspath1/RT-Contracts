package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.videoFile.VideoFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract factory interface with methods for all [VideoFile] operations
 */
interface VideoFileFactory {
    /**
     * Abstract command used to create a new [VideoFile] object
     *
     * @param request the [Create.Request] request
     * @return a [Command] object
     */
    fun create(request: Create.Request, uuid: String, profile: String): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command user to retrieve a list of [VideoFile] objects as a [Page]
     *
     * @param pageable the [Pageable] object, that has the page number and page size
     * @return a [Command] object
     */
    fun retrieveList(pageable: Pageable): Command<Page<VideoFile>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to list [VideoFile] objects between two creations dates
     *
     * @param request the [ListBetweenCreationDates.Request] request
     * @return a [Command] object
     */
    fun listBetweenCreationDates(request: ListBetweenCreationDates.Request): Command<List<VideoFile>, Multimap<ErrorTag, String>>
}