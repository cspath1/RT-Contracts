package com.radiotelescope.repository.log

import com.fasterxml.jackson.annotation.JsonInclude
import com.radiotelescope.repository.error.Error
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Log of an action in the web-application
 *
 * This Entity correlates to the Log SQL table
 */
@Entity
@Table(name = "log")
data class Log(
        @Column(name = "affected_table")
        @Enumerated(value = EnumType.STRING)
        var affectedTable: AffectedTable,
        @Column(name = "action")
        var action: String,
        @Column(name = "timestamp")
        var timestamp: Date,
        @Column(name = "affected_record_id")
        var affectedRecordId: Long? = null
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "user_id")
    var userId: Long? = null

    @OneToMany(mappedBy = "log", fetch = FetchType.EAGER)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var errors: MutableSet<Error> = mutableSetOf()

    @Column(name = "success")
    var isSuccess: Boolean = true

    enum class AffectedTable(val label: String) {
        USER("User"),
        USER_ROLE("User Role"),
        APPOINTMENT("Appointment"),
        RF_DATA("RF Data"),
        LOG("Log"),
        ERROR("Error"),
        RESET_PASSWORD_TOKEN("Reset Password Token"),
        UPDATE_EMAIL_TOKEN("Update Email Token"),
        ACTIVATE_ACCOUNT_TOKEN("Activate Account Token"),
        SUBSCRIBED_APPOINTMENT("Subscribed Appointment")
    }
}