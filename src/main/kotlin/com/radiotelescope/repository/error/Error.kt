package com.radiotelescope.repository.error

import com.fasterxml.jackson.annotation.JsonIgnore
import com.radiotelescope.repository.log.Log
import javax.persistence.*

@Entity
@Table(name = "error")
data class Error(
        @ManyToOne
        @JoinColumn(name = "log_id")
        @JsonIgnore
        var log: Log,
        @Column(name = "message")
        var message: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0
}