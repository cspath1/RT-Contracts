package com.radiotelescope.controller.spring

import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.repository.error.Error
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.UserContextImpl
import org.springframework.stereotype.Service
import java.util.*

@Service
class Logger(
        private var logRepo: ILogRepository,
        private var errorRepo: IErrorRepository,
        userRepo: IUserRepository,
        userRoleRepo: IUserRoleRepository
) {
    var userContext = UserContextImpl(
            userRepo = userRepo,
            userRoleRepo = userRoleRepo
    )

    fun createSuccessLog(info: Info) {
        val log = info.toEntity()
        userContext.currentUserId()?.let {
            log.userId = it
        }
        logRepo.save(log)
    }

    fun createErrorLogs(info: Info, errors: Map<String, Collection<String>>) {
        val log = info.toEntity()
        userContext.currentUserId()?.let {
            log.userId = it
        }
        logRepo.save(log)

        errors.keys.forEach {
            val errorList = errors[it]
            if (errorList != null && errorList.isNotEmpty()) {
                errorList.forEach { error ->
                    val err = generateErrorRecord(
                            log = log,
                            key = it,
                            message = error
                    )

                    log.errors.add(err)
                }
            }
        }

        logRepo.save(log)
    }

    private fun generateErrorRecord(log: Log, key: String, message: String): Error {
        val error = Error(
                log = log,
                field = key,
                message = message
        )

        return errorRepo.save(error)
    }

    data class Info(
            var affectedTable: Log.AffectedTable,
            var action: Log.Action,
            var timestamp: Date,
            var affectedRecordId: Long?
    ) : BaseCreateRequest<Log> {
        override fun toEntity(): Log {
            return Log(
                    affectedTable = affectedTable,
                    action = action,
                    timestamp = timestamp,
                    affectedRecordId = affectedRecordId
            )
        }
    }
}