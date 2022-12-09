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
     * @param request the [Submit.Request] object
     * @return a [Command] object
     */
    fun submit(request: Submit.Request): Command<FrontpagePicture, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to approve or deny (delete) a [FrontpagePicture] object
     *
     * @param request the [ApproveDeny.Request] object
     * @return a [Command] object
     */
    fun approveDeny(request: ApproveDeny.Request): Command<FrontpagePicture, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a complete list of the [FrontpagePicture] objects in the database
     *
     * @return a [Command] object
     */
    fun retrieveList() : Command<List<FrontpagePicture>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a complete list of approved [FrontpagePicture] objects in the database
     *
     * @return a [Command] object
     */
    fun retrieveApproved() : Command<List<FrontpagePicture>, Multimap<ErrorTag, String>>
}