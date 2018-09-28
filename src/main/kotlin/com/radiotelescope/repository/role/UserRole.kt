package com.radiotelescope.repository.role

import javax.persistence.*

@Entity
@Table(name = "user_role")
data class UserRole(
        @Column(name = "role")
        @Enumerated(EnumType.STRING)
        private var role: Role
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @Column(name = "user_id")
    private var userId: Long? = null

    constructor(userId: Long, role: Role) : this(role) {
        this.userId = userId
    }

    enum class Role {
        GUEST,
        STUDENT,
        RESEARCHER,
        MEMBER,
        ADMIN
    }
}

