package com.radiotelescope.controller.admin.user

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
/**
 *
 *
 * Controller to handle self-cancellation of an account.
 *
 * @param userWrapper [UserUserWrapper]
 * @return BaseRestController
 *
 */
class UserCancelController(logger: Logger,
val userWrapper: UserUserWrapper): BaseRestController(logger)
{

    @CrossOrigin("http://localhost:8081")
    @PutMapping("api/users/list/cancel/{userId}")

            /**
             *
             * This function calls the user cancel method on the userWrapper object.
             * @return [Unit]
             */

    fun execute(@PathVariable(value="userId") userId:Long?)
    {
        val errors = pageErrors()
        if (userId == null)
        {
            logger.createErrorLogs(info = Logger.createInfo(Log.AffectedTable.USER, Log.Action.DELETE, affectedRecordId = userId ),  errors=  errors.toStringMap())
        }
        userWrapper.cancel(userId)
        {
           //success case
            if (it.error == null)
            {
                logger.createSuccessLog(info = Logger.createInfo(Log.AffectedTable.USER, Log.Action.DELETE, userId))
                result = Result(it)
            }
            //error case
            if (it.error != null)
            {
             logger.createErrorLogs(info = Logger.createInfo(Log.AffectedTable.USER, Log.Action.DELETE, userId), errors = errors.toStringMap())

                result = Result(errors.toStringMap())
            }

        }?.let {
            //If we reach here, user did not pass validation
            logger.createErrorLogs(info = Logger.createInfo(Log.AffectedTable.USER, Log.Action.DELETE, userId), errors = errors.toStringMap())
            result = Result(errors.toStringMap())
        }
    }

    fun pageErrors(): HashMultimap<ErrorTag, String>
    {
    val errors = HashMultimap.create<ErrorTag, String>()
    errors.put(ErrorTag.ID, "Error in getting the userId to self-cancel an account")
        return errors
    }
}