package com.radiotelescope.contracts.frontpagePicture

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import com.radiotelescope.service.s3.IAwsS3DeleteService

/**
 * Base concrete implementation of the [FrontpagePictureFactory] interface
 *
 * @param frontpagePictureRepo the [IFrontpagePictureRepository] interface
 */
class BaseFrontpagePictureFactory (
    private val frontpagePictureRepo: IFrontpagePictureRepository,
    private val s3DeleteService: IAwsS3DeleteService
) : FrontpagePictureFactory {

    /**
     * Override of the [FrontpagePictureFactory] method that will
     * return a [Submit] command object
     */
    override fun submit(request: Submit.Request): Command<FrontpagePicture, Multimap<ErrorTag, String>> {
        return Submit(
                request = request,
                frontpagePictureRepo = frontpagePictureRepo
        )
    }

    /**
     * Override of the [FrontpagePictureFactory] method that will
     * return an [ApproveDeny] command object
     */
    override fun approveDeny(request: ApproveDeny.Request): Command<FrontpagePicture, Multimap<ErrorTag, String>> {
        return ApproveDeny(
                request = request,
                frontpagePictureRepo = frontpagePictureRepo,
                s3DeleteService = s3DeleteService
        )
    }
}