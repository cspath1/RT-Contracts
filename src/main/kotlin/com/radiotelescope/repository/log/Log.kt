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
        @Enumerated(value = EnumType.STRING)
        var action: Action,
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

    enum class AffectedTable {
        USER,
        USER_ROLE,
        APPOINTMENT,
        RF_DATA
    }

    enum class Action {
        CREATE,
        RETRIEVE,
        UPDATE,
        DELETE,
        LOG_IN,
        LIST_FUTURE_APPOINTMENT_BY_USER,
        LIST_FUTURE_APPOINTMENT_BY_TELESCOPE_ID,
        BAN
    }
}