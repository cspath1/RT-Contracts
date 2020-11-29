package com.radiotelescope.contracts.videoFile

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.videoFile.VideoFile
import com.radiotelescope.repository.videoFile.IVideoFileRepository

/**
 * Override of the [Command] interface used for VideoFile creation
 *
 * @param request the [Request] object
 * @param videoFileRepo the [IVideoFileRepository] interface
 * @param uuid the token used by the video file record service
 * @param profile the current application profile
 */
class Create(
    private val request: Request,
    private val videoFileRepo: IVideoFileRepository,
    private val uuid: String,
    private val profile: String
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [VideoFile] object and return
     * the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theVideoFile = request.toEntity()
            videoFileRepo.save(theVideoFile)
            return SimpleResult(theVideoFile.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * [Request] object.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (profile != "LOCAL")
                errors.put(ErrorTag.TOKEN, "Bad Authorization: Profile must be LOCAL")

            if(videoPath.isBlank())
                errors.put(ErrorTag.VIDEO_PATH, "Required Field")
            if(thumbnailPath.isBlank())
                errors.put(ErrorTag.THUMBNAIL_PATH, "Required Field")

            if(videoLength.isBlank())
                errors.put(ErrorTag.VIDEO_LENGTH, "Required Field")
            if(videoLength == "00:00:00")
                errors.put(ErrorTag.VIDEO_LENGTH, "Video Length Must Be Greater than 0 Seconds")
            if(videoLength.length != 8)
                errors.put(ErrorTag.VIDEO_LENGTH, "Incorrectly Formatted Video Length")

            if(token.isBlank())
                errors.put(ErrorTag.TOKEN, "Required Field")
            if(token != uuid)
                errors.put(ErrorTag.TOKEN, "Bad Authorization: Incorrect ID")

        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for video file creation.
     * Implements the [BaseCreateRequest] interface
     */
    data class Request (
            val thumbnailPath: String,
            val videoPath: String,
            val videoLength: String,
            val token: String
    ) : BaseCreateRequest<VideoFile> {
        override fun toEntity(): VideoFile {
            return VideoFile(
                    thumbnailPath = thumbnailPath,
                    videoPath = videoPath,
                    videoLength = videoLength
            )
        }
    }
}