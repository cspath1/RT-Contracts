package com.radiotelescope.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
@Configuration
class SecurityConfiguration(
        private var authenticationProvider: AuthenticationProvider
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        if (http != null) {
            http.csrf().disable()

            http.cors().and()
                    .authorizeRequests().antMatchers("/api/login**").permitAll()
                    .and()
                    .formLogin()
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .loginProcessingUrl("/api/login")

            http.cors().and()
                    .authorizeRequests().antMatchers(HttpMethod.POST, "/users/register").permitAll()
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.authenticationProvider(authenticationProvider)
    }
}