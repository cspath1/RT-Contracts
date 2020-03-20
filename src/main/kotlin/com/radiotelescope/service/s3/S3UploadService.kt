package com.radiotelescope.service.s3

import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import com.amazonaws.services.s3.transfer.model.UploadResult
import com.amazonaws.util.IOUtils
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.config.S3Configuration
import com.radiotelescope.contracts.SimpleResult
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

@Service
class S3UploadService(
        private val amazonS3Bucket: S3Bucket,
        private val s3Configuration: S3Configuration
) {
    fun execute(multipartFile: MultipartFile, uploadPath: String): SimpleResult<UploadResult, Multimap<ErrorTag, String>> {
        return try {
            val uploadResult = upload(multipartFile.inputStream, uploadPath)
            SimpleResult(uploadResult, null)
        } catch (e: IOException) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.UPLOAD, e.message)
            SimpleResult(null, errors)
        }
    }

    fun upload(inputStream: InputStream, uploadPath: String): UploadResult? {
        val metadata = ObjectMetadata()
        val transferManager = TransferManagerBuilder.standard().withS3Client(s3Configuration.getAmazonS3Client()).build()

        lateinit var byteArrayInputStream: ByteArrayInputStream

        try {
            val byteArray = IOUtils.toByteArray(inputStream)
            metadata.contentLength = byteArray.size.toLong()
            byteArrayInputStream = ByteArrayInputStream(byteArray)
            val putObjectRequest = PutObjectRequest(amazonS3Bucket.name, uploadPath, byteArrayInputStream, metadata)
            putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead

            val upload = transferManager.upload(putObjectRequest)

            return upload.waitForUploadResult()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            transferManager.shutdownNow(false)
            inputStream.close()
            byteArrayInputStream.close()
        }

        return null
    }
}