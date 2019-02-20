package com.radiotelescope.repository.accountActivateToken

import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing an Account activation token for the web-application
 *
 * This Entity correlates to the account_activation_token SQL Table
 */
@Entity
@Table(name = "account_activate_token")
data class AccountActivateToken(
        @Column(name = "token", unique = true, nullable = false)
        var token: String,
        @Column(name = "expiration_date", nullable = false)
        var expirationDate: Date
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User
}