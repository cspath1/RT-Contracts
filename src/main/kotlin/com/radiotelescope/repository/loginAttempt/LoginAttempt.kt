package com.radiotelescope.repository.loginAttempt

import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Login Attempt for the web-application
 *
 * This Entity correlates to the LoginAttempt SQL table
 */
@Entity
@Table(name = "login_attempt")
data class LoginAttempt(
        @Column(name = "login_time", nullable = false)
        var loginTime: Date
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User
}