package com.radiotelescope.security

import com.radiotelescope.controller.model.Profile
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration(
        val profile: Profile
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        if (profile == Profile.PROD) {
            registry.addMapping("/**")
                    .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
                    .allowedOrigins("*")
        }
    }
}