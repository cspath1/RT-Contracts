package com.radiotelescope.service.s3

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult

/**
 * Interface used for for retrieving files from AWS Simple Storage Service.
 * Keeping this as an interface allows the application to use test implementations
 * during testing that will not actually contact AWS.
 */
interface IAwsS3RetrieveService {
    /**
     * Retrieve file from S3 at the specified key
     *
     * @param key the location of the file in the bucket
     * @return a [SimpleResult] containing a result from the transaction
     */
    fun execute(key: String): SimpleResult<PhotoInfo, Multimap<ErrorTag, String>>
}