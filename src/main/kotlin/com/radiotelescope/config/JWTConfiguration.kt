package com.radiotelescope.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * JWT Token Configuration that specifies the location of the secret key
 * used to generate JWT tokens for users upon successful authentication
 *
 * @param secretKey the JWT Secret Key
 */
@Configuration
class JWTConfiguration(
        @Value("\${radio_telescope.jwt-secret}") private val secretKey: String
) {
    /**
     * Spring Bean that will return the JWT Secret Key
     */
    @Bean
    fun secretKey(): String {
        return secretKey
    }
}