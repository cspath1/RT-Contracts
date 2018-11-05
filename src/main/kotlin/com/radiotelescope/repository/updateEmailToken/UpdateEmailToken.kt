package com.radiotelescope.repository.updateEmailToken

import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "update_email_token")
data class UpdateEmailToken (
        @Column(name = "token", unique = true, nullable = false)
        var token: String,
        @Column(name = "expiration_date", nullable = false)
        var expirationDate: Date
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null
}