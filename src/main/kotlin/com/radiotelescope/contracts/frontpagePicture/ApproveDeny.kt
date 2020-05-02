package com.radiotelescope.contracts.frontpagePicture

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import com.radiotelescope.service.s3.IAwsS3DeleteService

/**
 * Override of the [Command] interface method used to approve or deny
 * a [FrontpagePicture] record
 *
 * @param request the [ApproveDeny.Request]
 * @param frontpagePictureRepo the [IFrontpagePictureRepository] interface
 * @param s3DeleteService the [IAwsS3DeleteService] interface
 */
class ApproveDeny (
        private val request: Request,
        private val frontpagePictureRepo: IFrontpagePictureRepository,
        private val s3DeleteService: IAwsS3DeleteService
) : Command<FrontpagePicture, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. The method allows admins to approve
     * or deny user-submitted [FrontpagePicture] records. If approved, the [FrontpagePicture.approved]
     * is updated to true. If denied, record is deleted.
     *
     * Any error will result in the method returning an error in the [SimpleResult].
     */
    override fun execute(): SimpleResult<FrontpagePicture, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val theFrontpagePicture = frontpagePictureRepo.findById(request.frontpagePictureId).get()

        // If the request is for approval, mark as approved
        if (request.isApprove) {
            theFrontpagePicture.approved = true
            frontpagePictureRepo.save(theFrontpagePicture)
        } else {
            // Delete the picture from S3
            s3DeleteService.execute(theFrontpagePicture.pictureUrl)
            // Remove from database
            frontpagePictureRepo.delete(theFrontpagePicture)
        }

        return SimpleResult(theFrontpagePicture, null)
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
            if(!frontpagePictureRepo.existsById(this.frontpagePictureId))
                errors.put(ErrorTag.ID, "Frontpage Picture does not exist")
        }

        return errors
    }

    /**
     * Data class containing the fields necessary to submit a frontpage picture
     *
     * @param frontpagePictureId the id of the relevant [FrontpagePicture]
     * @param isApprove admin approval or denial
     */
    data class Request (
            val frontpagePictureId: Long,
            val isApprove: Boolean
    )
}