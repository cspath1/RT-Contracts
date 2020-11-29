package com.radiotelescope.repository.appointment

import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.orientation.Orientation
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig
import com.radiotelescope.repository.user.User
import java.util.*
import javax.persistence.*

/**
 * Entity Class representing an Appointment for the web-application
 *
 * This Entity correlates to the Appointment SQL Table
 */
@Entity
@Table(name = "appointment")
data class Appointment(
        @Column(name = "start_time", nullable = false)
        var startTime: Date,
        @Column(name = "end_time", nullable = false)
        var endTime: Date,
        @Column(name = "telescope_id", nullable = false)
        var telescopeId: Long,
        @Column(name = "public", nullable = false)
        var isPublic: Boolean,
        @Column(name = "priority", nullable = false)
        @Enumerated(value = EnumType.STRING)
        var priority: Appointment.Priority,
        @Column(name = "type", nullable = false)
        @Enumerated(value = EnumType.STRING)
        var type: Type
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @OneToMany(mappedBy = "appointment")
    var coordinateList: MutableList<Coordinate> = mutableListOf()

    @OneToOne
    @JoinColumn(name = "orientation_id")
    var orientation: Orientation? = null

    @ManyToOne
    @JoinColumn(name = "celestial_body_id")
    var celestialBody: CelestialBody? = null

    @OneToOne
    @JoinColumn(name = "spectracyber_config_id")
    var spectracyberConfig: SpectracyberConfig? = null

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: Appointment.Status = Status.SCHEDULED

    enum class Status(val label: String) {
        REQUESTED("Requested"),
        SCHEDULED("Scheduled"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        CANCELED("Canceled")
    }
    
    enum class Type(val label: String) {
        POINT("Point"),
        CELESTIAL_BODY("Celestial Body"),
        RASTER_SCAN("Raster Scan"),
        DRIFT_SCAN("Drift Scan"),
        FREE_CONTROL("Free Control")
    }

    enum class Priority(val label: String){
        MANUAL("Manual"),
        PRIMARY("Primary"),
        SECONDARY("Secondary")
    }

    companion object {
        // 5 hours for guests
        const val GUEST_APPOINTMENT_TIME_CAP: Long = (5 * 60 * 60 * 1000)
        // 24 hours for students and alumni
        const val STUDENT_APPOINTMENT_TIME_CAP: Long = (24 * 60 * 60 * 1000)
        // 48 hours for members, alumni, and researchers
        const val MEMBER_APPOINTMENT_TIME_CAP: Long = (48 * 60 * 60 * 1000)
        const val RESEARCHER_APPOINTMENT_TIME_CAP: Long = (48 * 60 * 60 * 1000)
        const val ALUMNUS_APPOINTMENT_TIME_CAP: Long = (48 * 60 * 60 * 1000)
    }
}