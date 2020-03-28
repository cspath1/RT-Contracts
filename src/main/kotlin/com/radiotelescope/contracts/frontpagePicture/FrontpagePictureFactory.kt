package com.radiotelescope.contracts.frontpagePicture

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture

/**
 * Abstract factory interface with methods for all [FrontpagePicture] operations
 */
interface FrontpagePictureFactory {

    /**
     * Abstract command used to submit a [FrontpagePicture] object
     *
     * @return a [Command] object
     */
    fun submit(request: Submit.Request): Command<FrontpagePicture, Multimap<ErrorTag, String>>
}