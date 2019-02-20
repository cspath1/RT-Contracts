package com.radiotelescope.security

import com.radiotelescope.controller.model.Profile
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
/**
 * Allows the application to handle CORS requests
 *
 * @property profile the application [Profile]
 */
class WebConfiguration(
        val profile: Profile
) : WebMvcConfigurer {
    /**
     * Specifies the contents for CORS requests/responses
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        if (profile == Profile.PROD || profile == Profile.DEV) {
            registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "DELETE", "OPTIONS", "PUT")
                    .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
                            "Access-Control-Request-Headers")
                    .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                    .allowCredentials(true).maxAge(3600)
        }
    }
}