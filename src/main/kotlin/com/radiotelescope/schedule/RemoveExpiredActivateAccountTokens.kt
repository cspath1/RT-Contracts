package com.radiotelescope.schedule

import com.radiotelescope.repository.accountActivateToken.AccountActivateToken
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

/**
 * Spring Component that is executed every hour and will delete all (if any)
 * expired [AccountActivateToken] records and the user record associated with
 * it.
 *
 * @param accountActivateRepo the [IAccountActivateTokenRepository] interface
 * @param userRepo the [IUserRepository] interface
 * @param logRepo the [ILogRepository] interface
 */
@Component
class RemoveExpiredActivateAccountTokens(
        private val accountActivateRepo: IAccountActivateTokenRepository,
        private val userRepo: IUserRepository,
        private val logRepo: ILogRepository,
        private val userRoleRepo: IUserRoleRepository
) {
    /**
     * Iterate through all [AccountActivateToken] records (and associated user
     * records) that have expired, freeing up the email
     */
    @Scheduled(cron = Settings.EVERY_HOUR)
    fun execute() {
        accountActivateRepo.findAll().forEach {
            if (it.expirationDate.before(Date())) {
                accountActivateRepo.delete(it)
                userRoleRepo.deleteAll(userRoleRepo.findAllByUserId(it.user.id))
                userRepo.deleteById(it.user.id)
                createLogs(it)
            }
        }
    }

    /**
     * Function that will create logs for each of the records that were deleted
     * due to expired [AccountActivateToken] entities.
     *
     * @param accountActivateToken the [AccountActivateToken]
     */
    private fun createLogs(accountActivateToken: AccountActivateToken) {
        val activateAccountTokenLog = Log(
                affectedTable = Log.AffectedTable.ACTIVATE_ACCOUNT_TOKEN,
                action = "Remove Expired Token",
                timestamp = Date(),
                affectedRecordId = accountActivateToken.id,
                status = HttpStatus.OK.value()
        )

        val userLog = Log(
                affectedTable = Log.AffectedTable.USER,
                action = "Remove Non-Activated User",
                timestamp = Date(),
                affectedRecordId = accountActivateToken.user.id,
                status = HttpStatus.OK.value()
        )

        logRepo.saveAll(listOf(userLog, activateAccountTokenLog))
    }
}