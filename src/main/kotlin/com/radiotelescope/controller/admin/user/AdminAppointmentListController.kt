package com.radiotelescope.controller.admin.user

import com.google.common.collect.HashMultimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.appointment.AppointmentWrapper
import com.radiotelescope.contracts.appointment.ErrorTag
import com.radiotelescope.contracts.user.BaseUserFactory
import com.radiotelescope.contracts.user.UserFactory
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.UserContext
import com.radiotelescope.toStringMap
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminAppointmentListController
(

        userRoleRepo: IUserRoleRepository,
        userRepo: IUserRepository,
        private var context: UserContext,

        //why does it want me to autowire these?
        private var factory: UserFactory,
        private var aWrapper: AppointmentWrapper,
        logger: Logger
): BaseRestController(logger) {

    init {
        factory = BaseUserFactory(userRepo, userRoleRepo)
        aWrapper = AppointmentWrapper(context, factory)

    }


    @GetMapping(value = ["/appointments/list"])
    fun execute(@RequestParam("page") pageNumber: Int?, @RequestParam("size") pageSize: Int?) {
        val errors = HashMultimap.create<ErrorTag, String>()
        if (pageNumber == null || pageSize == null) {
            errors.put(ErrorTag.PAGE_PARAMS, "Invalid page parameters")


            logger.createErrorLogs(
                    info = Logger.createInfo
                    (
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = Log.Action.RETRIEVE,
                            affectedRecordId = null
                    ),
                    errors = errors.toStringMap()


            )
            result = Result(errors = errors.toStringMap())
        } else {
            //key line
            aWrapper.pageableAppointments(PageRequest.of(pageNumber, pageSize))
            { it ->
                it.success?.content?.forEach { logger.createSuccessLog(info = Logger.createInfo(Log.AffectedTable.APPOINTMENT, action = Log.Action.RETRIEVE, affectedRecordId = it.id)) }

            }
        }

        //If both are not null:
    }

}