package com.radiotelescope.contracts.frontpagePicture

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository


/**
 * Override of the [Command] interface method used to retrieve [FrontpagePicture]
 * information
 *
 * @param request the [Submit.Request]
 * @param frontpagePictureRepo the [IFrontpagePictureRepository] interface
 */
class Submit (
        private val request: Request,
        private val frontpagePictureRepo: IFrontpagePictureRepository
) : Command<FrontpagePicture, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. The method allows users to submit
     * frontpage pictures by uploading them to Amazon S3 and giving the database their url.
     * Pictures submitted by normal users wait for admin approval before being usable.
     *
     * Any error will result in the method returning an error in the [SimpleResult].
     */
    override fun execute(): SimpleResult<FrontpagePicture, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val newFrontpagePicture = FrontpagePicture(request.picture, request.description, request.approved)

        return SimpleResult(frontpagePictureRepo.save(newFrontpagePicture), null)
    }

    /**
     * Method responsible for constraint checking and validations for the
     * [Request] object.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>{
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (this.picture.length > 256)
                errors.put(ErrorTag.PICTURE, "Picture URL is too long")
            if (this.description.length > 256)
                errors.put(ErrorTag.DESCRIPTION, "Description is too long")
        }

        return errors
    }

    /**
     * Data class containing the fields necessary to submit a frontpage picture
     *
     * @param picture the S3 URL of the picture
     * @param description the description of the picture
     * @param approved the approval status of the picture
     */
    data class Request (
            val picture: String,
            val description: String,
            val approved: Boolean
    )
}