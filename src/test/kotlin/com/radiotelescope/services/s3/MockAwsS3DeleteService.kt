package com.radiotelescope.services.s3

import com.amazonaws.services.s3.transfer.model.UploadResult
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.service.s3.ErrorTag
import com.radiotelescope.service.s3.IAwsS3DeleteService

class MockAwsS3DeleteService(
        private val shouldSucceed: Boolean
) : IAwsS3DeleteService {
    override fun execute(key: String): SimpleResult<Boolean, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.DELETE, "Required field")

        return if (shouldSucceed) {
            SimpleResult<Boolean, Multimap<ErrorTag, String>>(shouldSucceed, null)
        } else {
            SimpleResult<Boolean, Multimap<ErrorTag, String>>(shouldSucceed, errors)
        }
    }
}