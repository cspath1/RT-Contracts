package com.radiotelescope.service.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.user.IUserRepository
import java.util.*

object Util {
    fun retrieveUrl(key: String, s3client: AmazonS3, s3Bucket: String): String {
        val isFile = !key.endsWith("/")

        if (isFile) {
            val urlRequest = GeneratePresignedUrlRequest(
                    s3Bucket,
                    key
            )

            val url = s3client.generatePresignedUrl(urlRequest)

            return url.toString()
        }

        return ""
    }

    fun generateUniqueFilename(originalFilename: String): String {
        val extensionIndex = originalFilename.lastIndexOf(".")
        val extension = originalFilename.substring(extensionIndex)

        return UUID.randomUUID().toString().replace("-", "") + extension
    }

    fun generateProfilePictureUploadPath(user: User): String {
        return user.firstName + user.lastName + "/"
    }
}