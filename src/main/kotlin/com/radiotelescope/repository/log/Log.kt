package com.radiotelescope.repository.log

import com.fasterxml.jackson.annotation.JsonInclude
import com.radiotelescope.repository.error.Error
import com.radiotelescope.repository.user.User
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
        @Column(name = "action")
        var action: String,
        @Column(name = "timestamp")
        var timestamp: Date,
        @Column(name = "affected_record_id")
        var affectedRecordId: Long? = null,
        @Column(name = "status")
        var status: Int
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "affected_table")
    @Enumerated(value = EnumType.STRING)
    var affectedTable: AffectedTable? = null

    @OneToOne
    @JoinColumn(name = "user_id")
    var user: User? = null

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
        VIDEO_FILE("Video File"),
        SENSOR_STATUS("Sensor Status"),
        LOG("Log"),
        ERROR("Error"),
        RESET_PASSWORD_TOKEN("Reset Password Token"),
        UPDATE_EMAIL_TOKEN("Update Email Token"),
        ACTIVATE_ACCOUNT_TOKEN("Activate Account Token"),
        ALLOTTED_TIME_CAP("Allotted Time Cap"),
        CELESTIAL_BODY("Celestial Body"),
        VIEWER("Viewer"),
        FEEDBACK("Feedback"),
        WEATHER_DATA("Weather Data"),
        SPECTRACYBER_CONFIG("Spectracyber Config"),
        THRESHOLDS("Thresholds"),
        SENSOR_OVERRIDES("Sensor Overrides"),
        FRONTPAGE_PICTURE("Frontpage Pictures")
    }
}