package com.radiotelescope.contracts.videoFile

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.videoFile.VideoFile
import java.util.*

/**
 * Override of the [Command] interface used to retrieve videos
 * between creation dates
 *
 * @param request the [Request] object
 * @param videoRepo the [IVideoFileRepository] interface
 */
class ListBetweenCreationDates(
    private val request: ListBetweenCreationDates.Request,
    private val videoRepo: IVideoFileRepository
) : Command<List<VideoFile>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes it will create a [List] of [VideoFile] objects and
     * return this in the [SimpleResult.success] value.
     *
     * If validation fails, it will will return the errors in a [SimpleResult.error]
     * value.
     */
    override fun execute(): SimpleResult<List<VideoFile>, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if(!errors.isEmpty){
            return SimpleResult(null, errors)
        }

        val list = videoRepo.findVideosCreatedBetweenDates(
                lowerDate = request.lowerDate,
                upperDate = request.upperDate
        )

        return SimpleResult(list, null)
    }

    /**
     * Method responsible for constraint checking and validations that
     * ensures the upperTime is greater than lowerTime
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (upperDate <= lowerDate)
                errors.put(ErrorTag.RECORD_CREATED_TIMESTAMP, "Upper request time bound cannot be less than or equal to lower request time bound")
        }

        return errors
    }

    /**
     * Data class containing all fields necessary for listing video file records between dates
     */
    data class Request(
            var lowerDate: Date,
            var upperDate: Date
    )
}