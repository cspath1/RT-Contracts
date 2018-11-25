package com.radiotelescope.service.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import org.springframework.stereotype.Service

@Service
class S3DeleteService(
        private val s3Client: AmazonS3,
        private val s3Bucket: S3Bucket,
        private val s3RetrieveService: S3RetrieveService
) {
    fun execute(key: String): SimpleResult<Boolean, Multimap<ErrorTag, String>> {
        val result = s3RetrieveService.execute(key)
        result.success?.let {
            val deleteObjectRequest = DeleteObjectRequest(
                    s3Bucket.name,
                    key
            )

            s3Client.deleteObject(deleteObjectRequest)

            return SimpleResult(true, null)
        } ?: let {
            return SimpleResult(false, result.error)
        }
    }
}