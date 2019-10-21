package com.radiotelescope.repository.videoFile

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [VideoFile] Entity
 */
@Repository
interface IVideoFileRepository: PagingAndSortingRepository<VideoFile, Long>