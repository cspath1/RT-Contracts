package com.radiotelescope.controller.frontpagePicture

import com.radiotelescope.contracts.celestialBody.Retrieve
import com.radiotelescope.contracts.frontpagePicture.UserFrontpagePictureWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.frontpagePicture.SubmitForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.frontpagePicture.FrontpagePicture
import com.radiotelescope.security.AccessReport
import com.radiotelescope.security.UserContext
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle Frontpage Picture submission
 *
 * @param frontpagePictureWrapper the [UserFrontpagePictureWrapper]
 * @param context the [UserContext]
 * @param roleRepo the [IUserRoleRepository]
 * @param logger the [Logger] service
 */
@RestController
class FrontpagePictureSubmitController(
        private val frontpagePictureWrapper: UserFrontpagePictureWrapper,
        private val context: UserContext,
        private val roleRepo: IUserRoleRepository,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Take in the picture and description of the new [FrontpagePicture]
     * and executing the [UserFrontpagePictureWrapper.submit] method.
     * If this method returns an [AccessReport], this means they did not pass
     * authentication and the method will respond with errors.
     *
     * Otherwise, this means the [Retrieve] command was executed, and the controller
     * will check whether or not this command was a success or not, responding
     * appropriately.
     */
    @PostMapping(value = ["/api/frontpage-picture/"])
    fun execute(@RequestParam("picture-title") pictureTitle: String,
                @RequestParam("picture-url") pictureUrl: String,
                @RequestParam("description") description: String): Result {
        // If the user is an Admin, picture is automatically approved
        val isAdmin = context.currentUserId() != null &&
                roleRepo.findAllApprovedRolesByUserId(context.currentUserId()!!).find { role -> UserRole.Role.ADMIN == role.role } != null

        val form = SubmitForm(
                pictureTitle = pictureTitle,
                pictureUrl = pictureUrl,
                description = description,
                approved = isAdmin
        )

        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.FRONTPAGE_PICTURE,
                            action = "Frontpage Picture Submission",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise, execute the wrapper command
        let {
            frontpagePictureWrapper.submit(
                    request = form.toRequest()
            ) { response ->
                response.success?.let { data ->
                    result = Result(
                            data = data
                    )

                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.FRONTPAGE_PICTURE,
                                    action = "Frontpage Picture Submission",
                                    affectedRecordId = data.id,
                                    status = HttpStatus.OK.value()
                            )
                    )
                }
                response.error?.let { errors ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.FRONTPAGE_PICTURE,
                                    action = "Frontpage Picture Submission",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(
                            errors = errors.toStringMap()
                    )
                }
            }?.let { report ->
                // If we get here, that means the User did not pass authentication

                // Set the errors depending on if the user was not authenticated or the
                // record did not exists
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.FRONTPAGE_PICTURE,
                                action = "Frontpage Picture Submission",
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
        }

        return result
    }
}