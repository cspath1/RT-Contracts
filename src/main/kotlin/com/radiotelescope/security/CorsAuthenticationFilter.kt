package com.radiotelescope.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter that, given the login request was attempted via CORS, will set the response to "OK"
 * so that the requesting website (i.e. the front-end) will handle the response correctly. It
 * will also add response headers that allow for CORS
 */
class CorsAuthenticationFilter : UsernamePasswordAuthenticationFilter() {
    /**
     * Override of the [UsernamePasswordAuthenticationFilter.attemptAuthentication] method that will handle CORS
     * authentication requests
     *
     * @param request the [HttpServletRequest]
     * @param response the [HttpServletResponse]
     * @return an [Authentication] object
     */
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