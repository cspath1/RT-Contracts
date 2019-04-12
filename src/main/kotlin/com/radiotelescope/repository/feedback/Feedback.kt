package com.radiotelescope.repository.feedback

import javax.persistence.*

/**
 * Entity Class representing an user feedback for the web-application
 *
 * This Entity correlates to the Feedback SQL Table
 */
@Entity
@Table(name = "feedback")
data class Feedback(
        @Column(name = "name")
        val name: String?,
        @Column(name = "priority")
        val priority: Int,
        @Column(name = "comments")
        val comments: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0
}