package com.radiotelescope.config

import com.radiotelescope.controller.model.Profile
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration(
        @Value("\${radio_telescope.profile}") private val appProfile: Profile
) {
    @Bean
    fun appProfile(): Profile {
        return appProfile
    }
}