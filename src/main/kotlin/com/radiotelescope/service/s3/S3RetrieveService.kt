package com.radiotelescope.service.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import org.springframework.stereotype.Service

@Service
class S3RetrieveService(
        private val amazonS3Client: AmazonS3,
        private val s3Bucket: S3Bucket
) {
    fun execute(key: String): SimpleResult<PhotoInfo, Multimap<ErrorTag, String>> {
        val getObjectRequest = GetObjectRequest(s3Bucket.name, key)

        lateinit var s3Object: S3Object

        try {
            s3Object = amazonS3Client.getObject(getObjectRequest)

            if (s3Object != null) {
                val url = Util.retrieveUrl(
                        key = s3Object.key,
                        s3client = amazonS3Client,
                        s3Bucket = s3Bucket
                )

                return SimpleResult(
                        success = PhotoInfo(
                                url = url,
                                key = s3Object.key
                        ),
                        error = null
                )
            } else {
                val errors =  HashMultimap.create<ErrorTag, String>()
                errors.put(ErrorTag.RETRIEVE, "An error occurred retrieving this image")
                return SimpleResult(null, errors)
            }
        } catch (e: AmazonS3Exception) {
            val errors = HashMultimap.create<ErrorTag, String>()
            if (e.errorCode == "NoSuchKey")
                errors.put(ErrorTag.NO_SUCH_KEY, e.errorMessage)
            else
                errors.put(ErrorTag.RETRIEVE, e.errorMessage)

            return SimpleResult(null, errors)
        } finally {
            s3Object.close()
        }
    }
}