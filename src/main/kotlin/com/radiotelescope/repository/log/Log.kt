package com.radiotelescope.repository.log

import java.util.*
import javax.persistence.*

@Entity
class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "user_id")
    var userId: Long = 0

    @Column(name = "affected_table")
    @Enumerated(value = EnumType.STRING)
    lateinit var affectedTable: AffectedTable

    @Column(name = "action")
    @Enumerated(value = EnumType.STRING)
    lateinit var action: Action

    @Column(name = "timestamp")
    lateinit var timestamp: Date

    @Column(name = "affected_record_id")
    var affectedRecordId: Long = 0

    enum class AffectedTable {
        USER,
        APPOINTMENT
    }

    enum class Action {
        CREATE,
        RETRIEVE,
        UPDATE,
        DELETE
    }
}