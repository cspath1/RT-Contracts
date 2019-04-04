package com.radiotelescope.repository.heartbeatMonitor

import com.radiotelescope.repository.telescope.RadioTelescope
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing the Heartbeat Monitor table, which is in charge
 * of monitor communication between the local server(s) and the remote server
 */
@Entity
@Table(name = "heartbeat_monitor")
class HeartbeatMonitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "last_communication")
    lateinit var lastCommunication: Date

    @OneToOne
    @JoinColumn(name = "telescope_id")
    lateinit var radioTelescope: RadioTelescope
}