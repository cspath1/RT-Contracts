package com.radiotelescope.service.s3

import com.amazonaws.services.s3.transfer.model.UploadResult
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import org.springframework.web.multipart.MultipartFile

/**
 * Interface used for for uploading files to the AWS Simple Storage Service.
 * Keeping this as an interface allows the application to use test implementations
 * during testing that will not actually contact AWS.
 */
interface IAwsS3UploadService {
    /**
     * Upload a file from S3 to the specified path
     *
     * @param multipartFile the file to upload as a [MultipartFile]
     * @param uploadPath the path to place the file into
     * @return a [SimpleResult] containing a result from the transaction
     */
    fun execute(multipartFile: MultipartFile, uploadPath: String): SimpleResult<UploadResult, Multimap<ErrorTag, String>>
}