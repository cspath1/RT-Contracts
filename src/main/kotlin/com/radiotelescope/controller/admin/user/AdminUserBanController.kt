package com.radiotelescope.controller.admin.user

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping

/**
 * Controller class for banning a user.
 * @param userWrapper of type [UserUserWrapper]
 * @return of type [BaseRestController]
 */

class AdminUserBanController(

        private val userWrapper: UserUserWrapper,
         logger: Logger

        ): BaseRestController(logger)
{
    @CrossOrigin(value = ["http://localhost:8081"])
    @PutMapping(value = ["api/users/list/{userId}"])

            /**
             * Execute function which calls the ban method on userWrapper, passing in the userId using the RESTful API.
             * Also populates the Logs
             * @return [Unit]
             */

fun execute(@PathVariable(value="userId") userId:Long?) {
        //error case
        val errors = pageErrors()
        if (userId == null) {

     logger.createErrorLogs(info = Logger.createInfo(
             affectedTable = Log.AffectedTable.USER,
             action = Log.Action.BAN,
             affectedRecordId = userId
     ),
             errors = errors.toStringMap()
             )
        }
        userWrapper.ban(userId)
        {

        if (it.error == null)
        {
            logger.createSuccessLog(info = Logger.createInfo(affectedTable = Log.AffectedTable.USER,
                    action = Log.Action.BAN,
                    affectedRecordId = userId
            ))
            result = Result(data = it )
        }
        else
        {
            logger.createErrorLogs(info = Logger.createInfo(affectedTable = Log.AffectedTable.USER,
                    action = Log.Action.BAN,
                    affectedRecordId = userId
            ), errors = errors.toStringMap())

            result = Result(errors = errors.toStringMap())
        }
        }?.let {
            //If we reach here, user did not pass validation
        logger.createErrorLogs(info = Logger.createInfo(Log.AffectedTable.USER, action = Log.Action.BAN, affectedRecordId = userId),
                errors = errors.toStringMap()
        )
           result = Result(errors = it.toStringMap())
        }
    }
    private fun pageErrors(): HashMultimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()
        errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")
        return errors
    }
}