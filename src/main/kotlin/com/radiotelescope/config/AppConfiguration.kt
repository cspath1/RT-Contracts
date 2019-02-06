package com.radiotelescope.config

import com.radiotelescope.controller.model.Profile
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Application Configuration that specifies the application profile
 * (currently production, development, or local) which dictates
 * application-specific functionality
 *
 * @param appProfile the application [Profile]
 */
@Configuration
class AppConfiguration(
        @Value("\${radio_telescope.profile}") private val appProfile: Profile
) {
    /**
     * Spring Bean that will return the application [Profile]
     *
     * @return the application [Profile]
     */
    @Bean
    fun appProfile(): Profile {
        return appProfile
    }
}