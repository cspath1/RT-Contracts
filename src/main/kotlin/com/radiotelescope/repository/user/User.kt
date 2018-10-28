package com.radiotelescope.repository.user

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import java.util.regex.Pattern
import javax.persistence.*
/**
 * Entity Class representing a User for the web-application
 *
 * This Entity correlates to the User SQL table
 */
@Entity
@Table(name = "user")
data class User(
        @Column(name = "first_name", nullable = false)
        var firstName: String,
        @Column(name = "last_name", nullable = false)
        var lastName: String,
        @Column(name = "email_address", nullable = false, unique = true)
        var email: String,
        @Column(name = "password", nullable = false)
        var password: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "company")
    var company: String? = null

    @Column(name = "phone_number")
    var phoneNumber: String? = null

    @Column(name = "active")
    var active: Boolean = false

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: User.Status = Status.Inactive

    enum class Status {
        Inactive,
        Active,
        Banned,
        Deleted
    }
    companion object {
        fun isEmailValid(email: String): Boolean {
            return Pattern.compile(
                    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }
        // Any of the following must also be over 8
        val passwordRegex = Regex("^((?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d)|" + // Uppercase, lowercase, digit
                "(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^a-zA-Z0-9])|" + // Uppercase, lowercase, special characters
                "(?=.*?[A-Z])(?=.*?\\d)(?=.*?[^a-zA-Z0-9])|" + // Uppercase, digit, special characters
                "(?=.*?[a-z])(?=.*?\\d)(?=.*?[^a-zA-Z0-9])).{8,}\$") // lowercase, digit, special characters

        const val passwordErrorMessage = "Passwords must be 8 characters long and have 3 or 4 of the following: " +
                "Upper Case, Lower Case, Special Character, Digit"

        val rtPasswordEncoder = Pbkdf2PasswordEncoder(
                "YCAS2018",
                50,
                256
        )

    }
}