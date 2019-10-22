package com.radiotelescope.controller.videoFile


import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.videoFile.VideoFile
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping

/**
 * Rest Controller to handle retrieving a video file path
 *
 * @param logger the [Logger] service
 */
@RestController
class VideoFileRetrieveController(
        private val videoRepo : IVideoFileRepository,
        logger: Logger
) : BaseRestController(logger) {
    @GetMapping(value = ["/api/videos"])
    fun execute(): Iterable<VideoFile> {
        val all = videoRepo.findAll()

        /*
        all.forEach() {
            print(it)
        }
        */

        print(all.iterator().next())

        return all
    }
}