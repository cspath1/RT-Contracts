package com.radiotelescope.repository.resetPasswordToken

import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*


/**
 * Entity Class representing a ResetPasswordToken for the web-application
 *
 * This Entity correlates to the ResetPasswordToken SQL Table
 */
@Entity
@Table(name = "reset_password_token")
data class ResetPasswordToken (
        @Column(name = "token", nullable = false)
        var token: String,
        @Column(name = "expiration_date", nullable = false)
        var expirationDate: Date
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

}