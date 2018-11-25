package com.radiotelescope.service.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest

class Util {
    companion object {
        fun retrieveUrl(key: String, s3client: AmazonS3, s3Bucket: S3Bucket): String {
            val isFile = !key.endsWith("/")

            if (isFile) {
                val urlRequest = GeneratePresignedUrlRequest(
                        s3Bucket.name,
                        key
                )

                val url = s3client.generatePresignedUrl(urlRequest)

                return url.toString()
            }

            return ""
        }
    }
}