package com.radiotelescope.repository.accountActivateToken

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [AccountActivateToken] Entity
 */
@Repository
interface IAccountActivateTokenRepository : CrudRepository<AccountActivateToken, Long> {
    /**
     * Spring Repository method that will see if any [AccountActivateToken] record
     * exists for the supplied token
     *
     * @param token the token
     * @return true or false
     */
    fun existsByToken(token: String): Boolean

    /**
     * Spring Repository method that grab a [AccountActivateToken] by the token
     *
     * @param token the token
     * @return a [AccountActivateToken]
     */
    fun findByToken(token: String): AccountActivateToken
}