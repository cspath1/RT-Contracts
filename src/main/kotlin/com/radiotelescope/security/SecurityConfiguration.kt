package com.radiotelescope.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import com.google.common.collect.ImmutableList
import com.radiotelescope.config.JWTConfiguration
import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@EnableWebSecurity
@Configuration
/**
 * Specifies security details specific to the application. Extends the
 * [WebSecurityConfigurerAdapter] class
 *
 * @param authenticationProvider the Spring Security [AuthenticationProvider]
 * @param profile the application [Profile]
 */
class SecurityConfiguration(
        private var authenticationProvider: AuthenticationProvider,
        private val userRepo: IUserRepository,
        private val profile: Profile,
        private val jwtConfiguration: JWTConfiguration
) : WebSecurityConfigurerAdapter() {

    /**
     * Configure the applications endpoints, specifically in regards to the
     * endpoints involved with Spring Security.
     *
     * If the application is running in production, it will require that all
     * requests happen over HTTPS.
     */
    override fun configure(http: HttpSecurity?) {
        if (http != null) {
            if (profile == Profile.PROD) {
                http.requiresChannel().anyRequest().requiresSecure()
                http.headers().httpStrictTransportSecurity()
            }

            http.csrf().disable()

            http.cors().and()
                    .authorizeRequests().antMatchers("/api/login**").permitAll()
                    .and()
                    .authorizeRequests().antMatchers(HttpMethod.POST, "/users/register").permitAll()
                    .and()
                    .formLogin()
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .loginProcessingUrl("/api/login")
                    .and()
                    .logout()
                        .logoutSuccessUrl("/login")
                        .logoutRequestMatcher(AntPathRequestMatcher("/api/logout"))
                    .and()
                    .addFilter(JWTAuthenticationFilter(authenticationManager(), jwtConfiguration))
                    .addFilter(JWTAuthorizationFilter(authenticationManager(), jwtConfiguration, userRepo))
                    .addFilter(CorsAuthenticationFilter())
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
    }

    /**
     * Override of the [WebSecurityConfigurerAdapter.configure] method that
     * specifies the [AuthenticationProvider] to use.
     */
    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.authenticationProvider(authenticationProvider)
    }

    /**
     * Allows for CORS requests to be made to the API endpoints
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = ImmutableList.of("*")
        configuration.allowedMethods = ImmutableList.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH")
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.allowCredentials = true
        // setAllowedHeaders is important! Without it, OPTIONS pre-flight request
        // will fail with 403 Invalid CORS request
        configuration.allowedHeaders = ImmutableList.of("Authorization", "Cache-Control", "Content-Type")
        configuration.exposedHeaders = ImmutableList.of("Authorization")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}