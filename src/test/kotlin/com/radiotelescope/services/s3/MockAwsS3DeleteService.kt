package com.radiotelescope.services.s3

import com.amazonaws.services.s3.transfer.model.UploadResult
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.service.s3.ErrorTag
import com.radiotelescope.service.s3.IAwsS3DeleteService
import org.springframework.web.multipart.MultipartFile

class MockAwsS3DeleteService(
        private val shouldSucceed: Boolean
) : IAwsS3DeleteService {
    override fun execute(key: String): SimpleResult<Boolean, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.DELETE, "Required field")

        val result = SimpleResult<Boolean, Multimap<ErrorTag, String>>(true, errors)

        return (if (shouldSucceed) {
            null
        } else {
            result
        }) as SimpleResult<Boolean, Multimap<ErrorTag, String>>
    }
}