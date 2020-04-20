package com.radiotelescope.controller.user

import com.radiotelescope.contracts.user.UpdateProfilePicture
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.service.s3.IAwsS3DeleteService
import com.radiotelescope.service.s3.IAwsS3UploadService
import com.radiotelescope.toStringMap
import liquibase.util.file.FilenameUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid

/**
 * REST Controller to handle User Profile Picture submission
 *
 * @param userRepo the [UserUserWrapper]
 * @param uploadService the [IAwsS3UploadService]
 * @param deleteService the [IAwsS3DeleteService]
 * @param context the [UserContext]
 * @param userRepo the [IUserRepository]
 * @param roleRepo the [IUserRoleRepository]
 * @param logger the [Logger] service
 */
@RestController
class UserUpdateProfilePictureController(
        private val userWrapper: UserUserWrapper,
        private val uploadService: IAwsS3UploadService,
        private val deleteService: IAwsS3DeleteService,
        private val context: UserContext,
        private val userRepo: IUserRepository,
        private val roleRepo: IUserRoleRepository,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Take in a submitted picture as a [MultipartFile] from a user or an admin,
     * then attempt to replace the picture in AWS S3 using [IAwsS3DeleteService]
     * and [IAwsS3UploadService]. If the process of deleting form or sending to S3 fails,
     * return an error.
     *
     * Generate a new filename using the [User]'s information and the current time. Then
     * attempt to update the User's profile picture information by executing the
     * [UserUserWrapper.updateProfilePicture] method. If this method returns an [AccessReport],
     * this means they did not pass authentication and the method will respond with errors.
     *
     * Otherwise, this means the [UpdateProfilePicture] command was executed, and the controller
     * will check whether or not this command was a success or not, responding
     * appropriately.
     */
    @PutMapping(value = ["/api/users/{userId}/profile-picture"], consumes = ["multipart/form-data"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestParam(value = "file") @Valid file: MultipartFile): Result {
        // If the user is an Admin, picture is automatically approved
        val isAdmin = context.currentUserId() != null &&
                roleRepo.findAllApprovedRolesByUserId(context.currentUserId()!!).find { role -> UserRole.Role.ADMIN == role.role } != null

        // If a profile picture exists from the user, delete it
        val theUser = userRepo.findById(userId).get()
        val thePicture = theUser.profilePicture
        if (thePicture != null) {
            val deleteResult = deleteService.execute(thePicture)
            if (deleteResult.error != null) {
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Update Profile Picture",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = deleteResult.error.toStringMap()
                )

                return Result(
                        errors = deleteResult.error.toStringMap()
                )
            }
        }

        // Generate a file path to replace the old one
        val generatedPath = theUser.firstName + theUser.lastName + System.currentTimeMillis() + "." + FilenameUtils.getExtension(file.originalFilename)
        val uploadResult = uploadService.execute(file, generatedPath)
        if (uploadResult.error != null) {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Update Profile Picture",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = uploadResult.error.toStringMap()
            )

            return Result(
                    errors = uploadResult.error.toStringMap()
            )
        }

        // Call the wrapper command
        userWrapper.updateProfilePicture(
                request = UpdateProfilePicture.Request(
                        id = userId,
                        profilePicture = generatedPath,
                        profilePictureApproved = isAdmin
                )
        ) { response ->
            // If the command was a success
            response.success?.let { data ->
                result = Result(
                        data = data
                )

                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Update",
                                affectedRecordId = data,
                                status = HttpStatus.OK.value()
                        )
                )
            }
            // Otherwise, it was a failure
            response.error?.let { error ->
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.USER,
                                action = "User Update",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = error.toStringMap()
                )
                result = Result(
                        errors = error.toStringMap()
                )
            }
        }?.let { report ->
            // If we get here, that means the user was not authenticated
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = "User Update",
                            affectedRecordId = null,
                            status = if (report.missingRoles != null) HttpStatus.FORBIDDEN.value() else HttpStatus.NOT_FOUND.value()
                    ),
                    errors = if (report.missingRoles != null) report.toStringMap() else report.invalidResourceId!!
            )

            // Set the errors depending on if the user was not authenticated or the
            // record did not exists
            result = if (report.missingRoles == null) {
                Result(errors = report.invalidResourceId!!, status = HttpStatus.NOT_FOUND)
            }
            // user did not have access to the resource
            else {
                Result(errors = report.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }
}