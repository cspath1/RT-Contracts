package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.videoFile.VideoFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface method used to retrieve [VideoFile]
 * information
 *
 * @param pageable the [Pageable] interface
 * @param videoFileRepo the [IVideoFileRepository] interface
 */
class RetrieveList (
        private val pageable: Pageable,
        private val videoFileRepo: IVideoFileRepository
) : Command<Page<VideoFile>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method.
     *
     * It will create a [Page] of [VideoFile] objects and
     * return this in the [SimpleResult.success] value.
     */
    override fun execute(): SimpleResult<Page<VideoFile>, Multimap<ErrorTag, String>> {
        val page = videoFileRepo.findAll(pageable)
        return SimpleResult(page, null)
    }
}