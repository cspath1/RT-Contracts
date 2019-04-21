package com.radiotelescope.repository.loginAttempt

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [LoginAttempt] Entity
 */
@Repository
interface ILoginAttemptRepository : CrudRepository<LoginAttempt, Long> {

    /**
     * Spring Repository method that will return a list of loginAttempt
     * for a User
     *
     * @param userId the User's Id
     * @return a [List] of [LoginAttempt]
     */
    @Query(value = "SELECT * " +
            "FROM login_attempt " +
            "WHERE user_id=?1",
            nativeQuery = true)
    fun findByUserId(userId: Long): List<LoginAttempt>

}