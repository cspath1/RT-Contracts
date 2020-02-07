package com.radiotelescope.repository.role

import com.radiotelescope.repository.user.User

import javax.persistence.*

/**
 * Entity class used to store a [User] Entity's roles. These roles
 * are used with Spring Security to validate the actions a [User] is
 * able to execute.
 *
 * This Entity correlates to the UserRole SQL Table.
 */
@Entity
@Table(name = "user_role")
data class UserRole(
        @Column(name = "role")
        @Enumerated(EnumType.STRING)
        var role: Role
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @Column(name = "approved")
    var approved: Boolean = false

    constructor(user: User, role: Role) : this(role) {
        this.user = user
    }

    enum class Role(val label: String){
        USER("User"),
        GUEST("Guest"),
        STUDENT("Student"),
        RESEARCHER("Researcher"),
        MEMBER("Member"),
        ADMIN("Admin"),
        ALUMNUS("Alumnus")
    }
}