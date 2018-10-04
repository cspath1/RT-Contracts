package com.radiotelescope.repository.error

import com.fasterxml.jackson.annotation.JsonIgnore
import com.radiotelescope.repository.log.Log
import javax.persistence.*

/**
 * Entity Class representing an Error for the web-application.
 * This is tied to the [Log] Entity and indicates a failed transaction
 *
 * This Entity correlates to the Error SQL table
 */
@Entity
@Table(name = "error")
data class Error(
        @ManyToOne
        @JoinColumn(name = "log_id")
        @JsonIgnore
        var log: Log,
        @Column(name = "key_field")
        var field: String,
        @Column(name = "message")
        var message: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0
}