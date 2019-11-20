package com.radiotelescope.controller.model.videoFile

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.videoFile.Create
import com.radiotelescope.contracts.videoFile.ErrorTag
import com.radiotelescope.controller.model.BaseForm
import java.util.*

/**
 * Create form that takes nullable versions of the [Create.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * into a [Create.Request] object.
 *
 * @param thumbnailPath the local path to the thumbnail file
 * @param videoPath the local path to the video file
 * @param videoLength the length of the video
 */
data class CreateForm (
    val thumbnailPath: String?,
    val videoPath: String?,
    val videoLength: String?,
    val token: String?
) : BaseForm<Create.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Create.Request] object
     *
     * @return the [Create.Request] object
     */
    override fun toRequest(): Create.Request {
        /*
        val times = videoLength!!.split(":")
        print(times.toString() + "\n")

        val videoLengthDuration = Time(times[0].toInt(), times[1].toInt(), times[2].toInt())
        print(videoLengthDuration.toString() + "\n")
        */

        //val currentTime = Date()

        return Create.Request(
                thumbnailPath = thumbnailPath!!,
                videoPath = videoPath!!,
                videoLength = videoLength!!,
                token = token!!
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (thumbnailPath == null)
            errors.put(ErrorTag.THUMBNAIL_PATH, "Required Field")
        if (videoPath == null)
            errors.put(ErrorTag.VIDEO_PATH, "Required Field")
        if (videoLength == null)
            errors.put(ErrorTag.VIDEO_LENGTH, "Required Field")
        if (token == null)
            errors.put(ErrorTag.TOKEN, "Required Field")

        return if (errors.isEmpty) null else errors
    }
}