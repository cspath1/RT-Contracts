package com.radiotelescope.repository.updateEmailToken

import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a token to update the user email
 *
 * This Entity correlates to the update_email_token SQL Table
 */
@Entity
@Table(name = "update_email_token")
data class UpdateEmailToken (
        @Column(name = "token", unique = true, nullable = false)
        var token: String,
        @Column(name = "expiration_date", nullable = false)
        var expirationDate: Date,
        @Column(name = "email_address", nullable = false)
        var email: String
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User
}