package com.radiotelescope.repository.allottedTimeCap

import com.radiotelescope.repository.user.User
import javax.persistence.*

/**
 * Entity Class representing a User's Allotted Appointment Time Cap.
 *
 * This Entity correlates to the Allotted Time Cap SQL Table
 */
@Entity
@Table(name = "allotted_time_cap")
data class AllottedTimeCap(
        @Column(name = "allotted_time")
        var allottedTime: Long?
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    constructor(user: User, allottedTime: Long?) : this(allottedTime) {
        this.user = user
    }
}