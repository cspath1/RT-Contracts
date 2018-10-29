package com.radiotelescope.repository.resetPasswordToken

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [ResetPasswordToken] Entity
 */
@Repository
interface IResetPasswordTokenRepository : CrudRepository<ResetPasswordToken, Long> {

    /**
     * Spring Repository method that will see if any [ResetPasswordToken] records exist
     * with the given token parameter
     *
     * @param token the token
     * @return true or false
     */
    fun existsByToken(token: String): Boolean


    /**
     * Spring Repository method that will grab a [ResetPasswordToken] by the token field
     *
     * @param token the token
     * @return a [ResetPasswordToken]
     */
    fun findByToken(token: String): ResetPasswordToken
}