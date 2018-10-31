package com.radiotelescope.controller.spring

import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.repository.error.Error
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.UserContext
import org.springframework.stereotype.Service
import java.util.*

/**
 * Spring service that will handle logging success and errors
 * into the database.
 *
 * @param logRepo the [ILogRepository] interface
 * @param errorRepo the [IErrorRepository] interface
 * @param userContext the [UserContext] interface
 */
@Service
class Logger(
        private var logRepo: ILogRepository,
        private var errorRepo: IErrorRepository,
        private var userContext: UserContext
) {
    /**
     * Used in REST controllers to log a successful action
     */
    fun createSuccessLog(info: Info) {
        val log = info.toEntity()
        userContext.currentUserId()?.let {
            log.userId = it
        }
        log.isSuccess = true
        logRepo.save(log)
    }

    /**
     * Used in REST controllers to log any failed transactions
     */
    fun createErrorLogs(info: Info, errors: Map<String, Collection<String>>) {
        val log = info.toEntity()
        userContext.currentUserId()?.let {
            log.userId = it
        }
        log.isSuccess = false
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

                    errorRepo.save(err)
                    log.errors.add(err)
                }
            }
        }

        logRepo.save(log)
    }

    /**
     * Private method to return a persisted [Error] record
     *
     * @param log the [Log] Entity
     * @param key the key from the errors HashMap
     * @param message the message associated with the HashMap key
     *
     * @return a persisted [Error] Entity
     */
    private fun generateErrorRecord(log: Log, key: String, message: String): Error {
        val error = Error(
                log = log,
                field = key,
                message = message
        )

        return errorRepo.save(error)
    }

    /**
     * Log Info data class that is used to persist the [Log] entity.
     * It implements the [BaseCreateRequest] interface
     */
    data class Info(
            var affectedTable: Log.AffectedTable,
            var action: String,
            var timestamp: Date,
            var affectedRecordId: Long?
    ) : BaseCreateRequest<Log> {
        /**
         * Override of the [BaseCreateRequest.toEntity] method to return a [Log] Entity
         */
        override fun toEntity(): Log {
            return Log(
                    affectedTable = affectedTable,
                    action = action,
                    timestamp = timestamp,
                    affectedRecordId = affectedRecordId
            )
        }
    }

    companion object {
        fun createInfo(
                affectedTable: Log.AffectedTable,
                action: String,
                affectedRecordId: Long?) : Info {
            return Info(
                    affectedTable = affectedTable,
                    action = action,
                    timestamp = Date(),
                    affectedRecordId = affectedRecordId
            )
        }
    }
}