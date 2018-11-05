package com.radiotelescope.repository.updateEmailToken

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [UpdateEmailToken] Entity
 */
@Repository
interface IUpdateEmailTokenRepository : CrudRepository<UpdateEmailToken, Long> {
    /**
     * Spring Repository method that will see if any [UpdateEmailToken] record
     * exists for the supplied token
     *
     * @param token the token
     * @return true or false
     */
    fun existsByToken(token: String): Boolean

    /**
     * Spring Repository method that grab a [UpdateEmailToken] by the token
     *
     * @param token the token
     * @return a [UpdateEmailToken]
     */
    fun findByToken(token: String): UpdateEmailToken
}