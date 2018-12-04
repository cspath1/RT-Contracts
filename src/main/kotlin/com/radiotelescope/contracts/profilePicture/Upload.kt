//package com.radiotelescope.contracts.profilePicture
//
//import com.google.common.collect.HashMultimap
//import com.google.common.collect.Multimap
//import com.radiotelescope.contracts.Command
//import com.radiotelescope.contracts.SimpleResult
//import com.radiotelescope.controller.model.Profile
//import com.radiotelescope.repository.profilePicture.IProfilePictureRepository
//import com.radiotelescope.repository.profilePicture.ProfilePicture
//import com.radiotelescope.repository.user.IUserRepository
//import com.radiotelescope.repository.user.User
//import com.radiotelescope.service.s3.S3UploadService
//import com.radiotelescope.service.s3.Util
//import org.springframework.web.multipart.MultipartFile
//
//class Upload(
//        private val userRepo: IUserRepository,
//        private val profilePictureRepo: IProfilePictureRepository,
//        private val s3UploadService: S3UploadService,
//        private val request: Request
//) : Command<Long, Multimap<ErrorTag, String>> {
//    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
//        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
//            val theUser = userRepo.findById(request.userId).get()
//
//            val filename = Util.generateUniqueFilename(request.multipartFile.originalFilename!!)
//            val uploadPath = Util.generateProfilePictureUploadPath(request.profile, theUser.accountHash)
//            val uploadFileLocation = uploadPath + filename
//
//            val uploadResult = s3UploadService.execute(request.multipartFile, uploadFileLocation)
//
//            uploadResult.success?.let { success ->
//                val theProfilePicture = generateProfilePicture(
//                        key = success.key,
//                        user = theUser
//                )
//
//                return SimpleResult(theProfilePicture.id, null)
//            }
//            uploadResult.error?.let {
//                val
//            }
//        }
//    }
//
//    private fun validateRequest(): Multimap<ErrorTag, String>? {
//        val errors = HashMultimap.create<ErrorTag, String>()
//        with(request) {
//            if (!userRepo.existsById(userId))
//                errors.put(ErrorTag.USER, "User #$userId not found")
//            if (multipartFile.originalFilename == null)
//                errors.put(ErrorTag.UPLOAD, "Invalid file")
//        }
//
//        return if (errors.isEmpty) null else errors
//    }
//
//    private fun generateProfilePicture(key: String, user: User): ProfilePicture {
//        val theProfilePicture = ProfilePicture(
//                profilePictureUrl = key,
//                user = user
//        )
//
//        theProfilePicture.validated = false
//
//        return profilePictureRepo.save(theProfilePicture)
//    }
//
//    data class Request(
//            val multipartFile: MultipartFile,
//            val userId: Long,
//            val profile: Profile
//    )
//}