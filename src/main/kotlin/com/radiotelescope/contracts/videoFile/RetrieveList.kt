package com.radiotelescope.contracts.videoFile

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.videoFile.VideoFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class RetrieveList (
        private val pageable: Pageable,
        private val videoFileRepo: IVideoFileRepository
) : Command<Page<VideoFile>, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Page<VideoFile>, Multimap<ErrorTag, String>> {
        val page = videoFileRepo.findAll(pageable)
        return SimpleResult(page, null)
    }
}