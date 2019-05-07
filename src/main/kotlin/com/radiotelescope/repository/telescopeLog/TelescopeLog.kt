package com.radiotelescope.repository.telescopeLog

import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Telescope Log for the control room application
 *
 * This Entity correlates to the Telescope Log SQL table
 */
@Entity
@Table(name = "telescope_log")
class TelescopeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @Column(name = "log_date")
    private lateinit var date: Date

    @Column(name = "log_level")
    private lateinit var logLevel: String

    @Column(name = "thread")
    private lateinit var thread: String

    @Column(name = "logger")
    private lateinit var logger: String

    @Column(name = "message")
    private lateinit var message: String

    fun getId(): Long {
        return id
    }

    fun getDate(): Date {
        return date
    }

    fun getLogLevel(): String {
        return logLevel
    }

    fun getThread(): String {
        return thread
    }

    fun getLogger(): String {
        return logger
    }

    fun getMessage(): String {
        return message
    }
}