package com.radiotelescope.service

import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import org.springframework.stereotype.Service
import java.util.*

@Service
class Logger(
        private var logRepo: ILogRepository
) {

    fun createSuccessLog(info: Info) {
        logRepo.save(info.toEntity())
    }

    data class Info(
            var userId: Long?,
            var affectedTable: Log.AffectedTable,
            var action: Log.Action,
            var timestamp: Date,
            var affectedRecordId: Long
    ) : BaseCreateRequest<Log> {
        override fun toEntity(): Log {
            val log = Log(
                    affectedTable = affectedTable,
                    action = action,
                    timestamp = timestamp,
                    affectedRecordId = affectedRecordId
            )

            if (userId != null)
                log.userId = userId

            return log
        }
    }
}