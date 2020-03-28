package com.radiotelescope.contracts.frontpagePicture

import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository

/**
 * Base concrete implementation of the [FrontpagePictureFactory] interface
 *
 * @param frontpagePictureRepo the [IFrontpagePictureRepository] interface
 */
class BaseFrontpagePictureFactory (
    private val frontpagePictureRepo: IFrontpagePictureRepository
) : FrontpagePictureFactory {

}