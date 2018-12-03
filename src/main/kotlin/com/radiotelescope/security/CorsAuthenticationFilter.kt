package com.radiotelescope.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CorsAuthenticationFilter : UsernamePasswordAuthenticationFilter() {
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        if (request != null && response != null) {
            if (request.getHeader("Origin") != null) {
                response.addHeader("Access-Control-Allow-Origin", "*")
                response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                response.addHeader("Access-Control-Allow-Credentials", "true")
                response.addHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
            }
            if (request.method == "OPTIONS") {
                response.writer.print("OK")
                response.writer.flush()
            }
        }

        return super.attemptAuthentication(request, response)
    }
}