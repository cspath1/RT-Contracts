package com.radiotelescope.repository.resetPasswordToken

import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*


/**
 * Entity Class representing a ResetPasswordToken for the web-applicaiton
 *
 * This Entity correlates to the ResetPasswordToken SQL Table
 */
@Entity
@Table(name = "reset_password_token")
data class ResetPasswordToken (
        @Column(name = "token", unique = true, nullable = false)
        var token: String,
        @Column(name = "expiry_date", nullable = false)
        var expiryDate: Date
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

}