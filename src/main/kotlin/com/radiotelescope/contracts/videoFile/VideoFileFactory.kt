package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.videoFile.VideoFile

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
    fun create(request: Create.Request, id: String): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to list [VideoFile] objects between two creations dates
     *
     * @param request the [ListBetweenCreationDates.Request] request
     * @return a [Command] object
     */
    fun listBetweenCreationDates(request: ListBetweenCreationDates.Request): Command<List<VideoFile>, Multimap<ErrorTag, String>>
}