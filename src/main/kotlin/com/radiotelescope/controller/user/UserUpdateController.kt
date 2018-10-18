package com.radiotelescope.controller.user

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.Update
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.user.UpdateForm
import com.radiotelescope.controller.model.Result
import com.radiotelescope.toStringMap
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to handle update User information
 *
 * @param userWrapper the [UserUserWrapper]
 * @param logger the [Logger] service
 */
@RestController
class UserUpdateController(
        private val userWrapper: UserUserWrapper,
        logger: Logger
) : BaseRestController(logger){

    @PutMapping(value = ["/users/{userId}/update"])
    fun execute(@PathVariable("userId") userId: Long,
                @RequestBody form: UpdateForm
    ): Result{
        // If the supplied path variable is not null, check if

        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.USER,
                            action = Log.Action.UPDATE,
                            affectedRecordId = null
                    ),
                    errors = it.toStringMap()
            )
            result = Result(errors = it.toStringMap())
        }
        // Otherwise execute the wrapper command
        userId?.let{ _ ->
            userWrapper.update(
                    request = form.toRequest()
            ){ it ->
                it.success?.let{
                    result = Result(
                            data = it
                    )

                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.USER,
                                    action = Log.Action.UPDATE,
                                    affectedRecordId = it
                            )
                    )
                }
                it.error?.let{
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.USER,
                                    action = Log.Action.UPDATE,
                                    affectedRecordId = null
                            ),
                            errors = it.toStringMap()
                    )
                    result = Result(
                            errors = it.toStringMap()
                    )
                }
            }


        }
        return result
    }

    /**
     * private method that returns a [HashMultimap] of errors
     * in the event the id passed in is null
     */
    private fun idErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.ID, "Invalid User Id")
        return errors
    }
}