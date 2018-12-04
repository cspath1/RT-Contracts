package com.radiotelescope.contracts.profilePicture

import com.radiotelescope.service.s3.ErrorTag

enum class ErrorTag {
    FILE,
    USER,
    UPLOAD,
    RETRIEVE
}