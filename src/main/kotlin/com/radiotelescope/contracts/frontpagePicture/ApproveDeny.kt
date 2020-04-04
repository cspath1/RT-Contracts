package com.radiotelescope.contracts.frontpagePicture

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository

class ApproveDeny (
        private val request: Request,
        private val frontpagePictureRepo: IFrontpagePictureRepository
) : Command<FrontpagePicture, Multimap<ErrorTag, String>> {

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
            // Remove from database
            frontpagePictureRepo.delete(theFrontpagePicture)
        }

        return SimpleResult(theFrontpagePicture, null)
    }

    private fun validateRequest(): Multimap<ErrorTag, String>{
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if(!frontpagePictureRepo.existsById(this.frontpagePictureId))
                errors.put(ErrorTag.ID, "Frontpage Picture does not exist")
        }

        return errors
    }

    data class Request (
            val frontpagePictureId: Long,
            val isApprove: Boolean
    )
}