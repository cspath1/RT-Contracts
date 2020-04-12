package com.radiotelescope.services.s3

import com.amazonaws.services.s3.transfer.model.UploadResult
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.service.s3.ErrorTag
import com.radiotelescope.service.s3.IAwsS3UploadService
import org.springframework.web.multipart.MultipartFile

class MockAwsS3UploadService(
        private val shouldSucceed: Boolean
) : IAwsS3UploadService {
    override fun execute(multipartFile: MultipartFile, uploadPath: String): SimpleResult<UploadResult, Multimap<ErrorTag, String>>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.UPLOAD, "Required field")

        val uploadResult = UploadResult()
        val result = SimpleResult<UploadResult, Multimap<ErrorTag, String>>(uploadResult, errors)

        return if (shouldSucceed) {
            null
        } else {
            result
        }
    }
}