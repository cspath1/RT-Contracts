package com.radiotelescope.service.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.google.common.collect.Multimap
import com.radiotelescope.config.S3Configuration
import com.radiotelescope.contracts.SimpleResult
import org.springframework.stereotype.Service

@Service
class S3DeleteService(
        private val s3Client: AmazonS3,
        private val s3Configuration: S3Configuration,
        private val s3RetrieveService: S3RetrieveService
) : IAwsS3DeleteService {
    override fun execute(key: String): SimpleResult<Boolean, Multimap<ErrorTag, String>> {
        val result = s3RetrieveService.execute(key)
        result.success?.let {
            val deleteObjectRequest = DeleteObjectRequest(
                    s3Configuration.getS3Bucket(),
                    key
            )

            s3Client.deleteObject(deleteObjectRequest)

            return SimpleResult(true, null)
        }
        return SimpleResult(false, result.error)
    }
}